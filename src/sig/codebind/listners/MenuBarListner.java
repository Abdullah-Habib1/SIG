package sig.codebind.listners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import sig.codebind.SalesInvoiceGenerator;

public class MenuBarListner implements ActionListener{

	private SalesInvoiceGenerator salesInvoiceGenerator;

	public MenuBarListner(SalesInvoiceGenerator salesInvoiceGenerator) {
		this.salesInvoiceGenerator = salesInvoiceGenerator;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == salesInvoiceGenerator.getSaveFile()) {
			salesInvoiceGenerator.toCsv(salesInvoiceGenerator.getInvoicesTable(), new File("src/resources/InvoiceHeader.csv"));
			salesInvoiceGenerator.toCsvFromList(salesInvoiceGenerator.getInvoiceItems(), new File("src/resources/InvoiceLine.csv"));
		}

		else if (e.getSource() == salesInvoiceGenerator.getLoadFile()) {
			salesInvoiceGenerator.getInvoices().addAll(salesInvoiceGenerator.fromCsv(new File("src/resources/InvoiceHeader.csv")));
		}

	}

}
