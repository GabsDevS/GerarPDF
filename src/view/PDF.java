package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfWriter;

import model.PDFMerger;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class PDF extends JFrame {

	private JPanel contentPane;
	private JTextField txtFolder;
	private JTextField txtCapas;
	private JLabel lblPdfUnificado;
	private JTextField txtFolderUnificado;


	public static void main(String[] args) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | javax.swing.UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException  ex) {
			System.err.println(ex.getMessage());
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PDF frame = new PDF();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PDF() {
		setTitle("GERAR PDF");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 376);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtFolder = new JTextField();
		txtFolder.setBounds(34, 52, 359, 25);
		contentPane.add(txtFolder);
		txtFolder.setColumns(10);

		JButton btnGerar = new JButton("Gerar");
		btnGerar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					processar(txtFolder.getText(), txtCapas.getText(), txtFolderUnificado.getText());
					JOptionPane.showMessageDialog(null, "Capas geradas e Pdf Unificado !");
					
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Erro Inesperado !");
				}
				
				
			}
		});
		btnGerar.setBounds(174, 284, 84, 23);
		contentPane.add(btnGerar);
		
		JLabel lblPastaOrigem = new JLabel("Pasta de Origem:");
		lblPastaOrigem.setBounds(34, 27, 117, 14);
		contentPane.add(lblPastaOrigem);
		
		JLabel lblPastaDeCapas = new JLabel("Pasta de Capas:");
		lblPastaDeCapas.setBounds(34, 96, 117, 14);
		contentPane.add(lblPastaDeCapas);
		
		txtCapas = new JTextField();
		txtCapas.setColumns(10);
		txtCapas.setBounds(34, 121, 359, 25);
		contentPane.add(txtCapas);
		
		lblPdfUnificado = new JLabel("PDF Unificado:");
		lblPdfUnificado.setBounds(34, 170, 117, 14);
		contentPane.add(lblPdfUnificado);
		
		txtFolderUnificado = new JTextField();
		txtFolderUnificado.setColumns(10);
		txtFolderUnificado.setBounds(34, 195, 359, 25);
		contentPane.add(txtFolderUnificado);
	}

	public static void processar(String folder_source, String folder_capas, String folder_unificado) throws Exception {

		List<InputStream> inputPdfList = new ArrayList<InputStream>();
		
		String folderOrigem = folder_source + "\\";
		String folderCapas = folder_capas + "\\";
		String folderUnificado = folder_unificado + "\\";
		
		validacaoDiretorio(folderOrigem);
		validacaoDiretorio(folderCapas);
		validacaoDiretorio(folderUnificado);
		
		String pasta = folderOrigem;
		File file = new File(pasta);

		// Percorre minha lista de arquivos
		for (String name : file.list()) {

			// Verifica o que é apenas PDF
			if (name.indexOf(".pdf") >= 0) {

				Document document = new Document(PageSize.A4, 20, 20, 210, 0);
				try {

					String capa = folderCapas + "Capa - " + String.format(name);

					PdfWriter.getInstance(document, new FileOutputStream(capa)); // criar o pdf

					inputPdfList.add(new FileInputStream(capa));
					System.out.println(capa);
					inputPdfList.add(new FileInputStream(pasta + name));
					System.out.println(pasta + name);

					document.open();

					// adicionando um parágrafo no documento

					Paragraph texto = new Paragraph(tratamentoNomeArquivos(name.replace(".pdf", "")),
							new Font(FontFamily.TIMES_ROMAN, 45, Font.BOLD));

					texto.setAlignment(Element.ALIGN_CENTER);

					document.add(texto);

				} catch (DocumentException de) {
					System.err.println(de.getMessage());
				} catch (IOException ioe) {
					System.err.println(ioe.getMessage());
				}
				document.close();
			}
		}

		OutputStream outputStream = new FileOutputStream(folderUnificado + "Merger.pdf");
		PDFMerger.mergePdfFiles(inputPdfList, outputStream);
	}

	public static String tratamentoNomeArquivos(String str) {
		str = str.replace("–", "-").replace("  ", " ");

		String[] words = str.split(" ");
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < words.length; i++) {

			sb.append(words[i].substring(0, 1).toUpperCase() + words[i].substring(1));
			if (words[i].equals("-")) {
				sb.append(String.format("%n"));
			}

			sb.append(" ");

		}

		return sb.toString().replace("  ", " ").replace(" -", "");

	}
	
	public static void validacaoDiretorio(String diretorio) {
		if (! new File(diretorio).exists()) {
			new File(diretorio).mkdir();
		}
	}
}
