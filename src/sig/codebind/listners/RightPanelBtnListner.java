package sig.codebind.listners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import sig.codebind.SalesInvoiceGenerator;
import sig.codebind.dialogs.InvoiceItemDialog;

public class RightPanelBtnListner implements ActionListener{

	private SalesInvoiceGenerator salesInvoiceGenerator;
	
	public RightPanelBtnListner(SalesInvoiceGenerator salesInvoiceGenerator) {
		this.salesInvoiceGenerator = salesInvoiceGenerator;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "createNewInvoiceItem":
			displayNewItemDialog();
			break;
		case "createItemOK":
			  createItemOK();
			break;
		case "createItemCancel":
			createItemCancel();
			break;
		case "saveChange":
			saveChange();
			break;
		case "cancel":
			cancel();
			break;
		}
	}
	
	private void cancel() {
		salesInvoiceGenerator.dispose();
	}

	private void saveChange() {
		salesInvoiceGenerator.toCsv(salesInvoiceGenerator.getInvoicesTable(), new File("src/resources/InvoiceHeader.csv"));
		salesInvoiceGenerator.toCsvFromList(salesInvoiceGenerator.getInvoiceItems(), new File("src/resources/InvoiceLine.csv"));
	}

	private void createItemOK() {
		String invoiceNum = salesInvoiceGenerator.getInvoiceItemDialog().getInvoiceNoField().getText();
		String itemName = salesInvoiceGenerator.getInvoiceItemDialog().getItemNameField().getText();
		String itemPriceStr = salesInvoiceGenerator.getInvoiceItemDialog().getItemPriceField().getText();
		String itemCountStr = salesInvoiceGenerator.getInvoiceItemDialog().getItemCountField().getText();
		salesInvoiceGenerator.getInvoiceItemDialog().setVisible(false);
		salesInvoiceGenerator.getInvoiceItemDialog().dispose();
		salesInvoiceGenerator.setInvoiceItemDialog(null);
		double itemPrice = Double.parseDouble(itemPriceStr);
		int itemCount = Integer.parseInt(itemCountStr);
		long total = (long) (itemPrice * itemCount);
		
		StringJoiner joiner = new StringJoiner(",");
		joiner.add(invoiceNum).add(itemName).add(itemPriceStr).add(itemCountStr);
		String insertedItem = joiner.toString();
		
		salesInvoiceGenerator.getInvoiceItems().add(insertedItem);
		salesInvoiceGenerator.getInvoicesDetailsTableModel().insertRow(salesInvoiceGenerator.getInvoicesDetailsTableModel().getRowCount(), 
				new Object[] {invoiceNum,itemName,itemPrice,itemCount,total});
		
		salesInvoiceGenerator.getInvoicesDetailsTableModel().fireTableDataChanged();
		
		Long totalAmount = salesInvoiceGenerator.getInvoiceItems().stream()
		.filter(it -> it.startsWith(String.valueOf(invoiceNum)))
		.map(it -> it.split(","))
		.map(m -> Long.valueOf(m[2]) * Long.valueOf(m[3]))
		.map(n -> Long.valueOf(n)).collect(Collectors.summingLong(Long::longValue));
		
		salesInvoiceGenerator.getInvoiceTotalValueLabel().setText(String.valueOf(totalAmount));
		
		int selectedRow = salesInvoiceGenerator.getInvoicesTable().getSelectedRow();
		
		salesInvoiceGenerator.getInvoicesTableModel().setValueAt(totalAmount, selectedRow, 3);
		
	}

	private void createItemCancel() {
		salesInvoiceGenerator.getInvoiceItemDialog().setVisible(false);
		salesInvoiceGenerator.getInvoiceItemDialog().dispose();
		salesInvoiceGenerator.setInvoiceItemDialog(null);
	}

	private void displayNewItemDialog() {
		salesInvoiceGenerator.setInvoiceItemDialog(new InvoiceItemDialog(salesInvoiceGenerator));
		salesInvoiceGenerator.getInvoiceItemDialog().setVisible(true);
	}
}
