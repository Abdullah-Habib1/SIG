package sig.codebind.listners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import sig.codebind.SalesInvoiceGenerator;

public class LeftPanelBtnListner implements ActionListener, ListSelectionListener,MouseListener{

	private SalesInvoiceGenerator salesInvoiceGenerator;

	public LeftPanelBtnListner(SalesInvoiceGenerator salesInvoiceGenerator) {
		this.salesInvoiceGenerator = salesInvoiceGenerator;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		if(!e.getValueIsAdjusting()) {
		Action action = (Action) new AbstractAction()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		        TableCellListener tcl = (TableCellListener)e.getSource();
		        System.out.println("Row   : " + tcl.getRow());
		        System.out.println("Column: " + tcl.getColumn());
		        System.out.println("Old   : " + tcl.getOldValue());
		        System.out.println("New   : " + tcl.getNewValue());
		        
		        viewDetails(tcl.getRow());
		    }
		};
		
		TableCellListener tcl = new TableCellListener(salesInvoiceGenerator.getInvoicesTable(), action);
		
	    
		}
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == salesInvoiceGenerator.getCreateNewInvoiceBtn()) {
			salesInvoiceGenerator.getInvoicesTableModel().insertRow(salesInvoiceGenerator.getInvoicesTableModel().getRowCount(), new Object[] {});
		}else if(e.getSource() == salesInvoiceGenerator.getDeleteInvoiceBtn()) {
			int numRows = salesInvoiceGenerator.getInvoicesTable().getSelectedRow();
			
			TableModel model = salesInvoiceGenerator.getInvoicesTable().getModel();
			
			Object numType = model.getValueAt(numRows, 0);
			
			salesInvoiceGenerator.getInvoicesTableModel().removeRow(numRows);
			
			Long numValue = null;

			if (numType instanceof Long) {
				numValue = (Long) numType;
			} else if (numType instanceof String) {
				numValue = Long.valueOf((String) numType);
			}

			Long invoiceNum = numValue;
			
			Predicate<String> matchInvoiceNum = i -> i.startsWith(String.valueOf(invoiceNum));
			
			salesInvoiceGenerator.getInvoiceItems().removeIf(x -> matchInvoiceNum.test(x));
			
		}
	}
	
	private void viewDetails(Integer row) {
		List<String> res = salesInvoiceGenerator.getInvoiceItems();
		int selectedRow = 0;
		resetOnNewSelection();
		if(row == null) {
			selectedRow = salesInvoiceGenerator.getInvoicesTable().getSelectedRow();
		}else {
			selectedRow = row;
		}
		 

		Vector selectedData = salesInvoiceGenerator.getInvoicesTableModel().getDataVector().get(selectedRow);

		String data = selectedData.toString().replace("[", "").replace("]", "").trim();

		Object numType = salesInvoiceGenerator.getInvoicesTableModel().getDataVector().get(selectedRow).elementAt(0);

		Long numValue = null;

		if (numType instanceof Long) {
			numValue = (Long) numType;
		} else if (numType instanceof String) {
			numValue = Long.valueOf((String) numType);
		}

		Long invoiceNum = numValue;

		int numberInvoice = 0;
		int date = 1;
		int customer = 2;

		int number = 0;
		int itemName = 1;
		int itemPrice = 2;
		int count = 3;

		Long invoiceTotal = res.stream().filter(it -> it.startsWith(String.valueOf(invoiceNum)))
				.map(t -> t.split(",")).map(m -> Long.valueOf(m[itemPrice]) * Long.valueOf(m[count]))
				.map(n -> Long.valueOf(n)).collect(Collectors.summingLong(Long::longValue));

		String[] tokenss = data.split(",");
		salesInvoiceGenerator.getInvoiceTotalValueLabel().setText(String.valueOf(invoiceTotal));
		String invoiceNumber = tokenss[numberInvoice];
		if (!isEmptyOrNull(invoiceNumber)) {
			salesInvoiceGenerator.getInvoiceNumValueLabel().setText(invoiceNumber);
		}

		String invoiceDate = tokenss[date];
		if (!isEmptyOrNull(invoiceDate)) {
			salesInvoiceGenerator.getInvoiceDateTextField().setText(invoiceDate);
		}

		String customerName = tokenss[customer];
		if (!isEmptyOrNull(customerName)) {
			salesInvoiceGenerator.getCustomerNametextField().setText(customerName);
		}

		List<String> invoiceItems = res.stream().filter(x -> x.startsWith(String.valueOf(invoiceNum)))
				.collect(Collectors.toList());

		salesInvoiceGenerator.getInvoicesDetailsTableModel().setRowCount(0);
		invoiceItems.forEach(it -> {
			String[] tokens = it.split(",");
			int total = Integer.valueOf(tokens[itemPrice]) * Integer.valueOf(tokens[count]);
			salesInvoiceGenerator.getInvoicesDetailsTableModel().insertRow(salesInvoiceGenerator.getInvoicesDetailsTableModel().getRowCount(),
					new Object[] { Long.parseLong(tokens[number]), tokens[itemName], tokens[itemPrice],
							tokens[count], total });
		});
		
		
	}

	private void resetOnNewSelection() {
		salesInvoiceGenerator.getInvoiceNumValueLabel().setText("");
		salesInvoiceGenerator.getInvoiceDateTextField().setText("");
		salesInvoiceGenerator.getInvoiceTotalValueLabel().setText("");
		salesInvoiceGenerator.getCustomerNametextField().setText("");
	}
	
	private boolean isEmptyOrNull(String value) {
		return ("null").equals(value.trim()) || value == null || ("").equals(value.trim());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		viewDetails(null);	
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
