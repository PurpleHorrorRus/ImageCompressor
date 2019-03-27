import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static JTable table;
	static JLabel totalFilesLabel, sizeAfterLabel, totalCompressed;
	static JTextField textField;
	static JProgressBar progressBar;
	static String previewFileName = "";
	
	static int count = 0;
	static double lenght = 0.0;
	static JButton button_3, previewButton, clearListButton, addFolderButton, addButton, delButton;
	static boolean clearing = false;
	
	final static String[] COL_NAMES = {"№", "Имя файла", "Размер файла", "Расположение"};
    static DefaultTableModel model = new DefaultTableModel(COL_NAMES, 0) {
		private static final long serialVersionUID = 5953145385105838308L;

		@Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame frame = new Frame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();	
				}
			}
		});
	}

	public Frame() {
		setResizable(false);
		setTitle("Image compressor by Nikiforov");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 856, 209);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		
		contentPane.setLayout(null);
		
		addFolderButton = new JButton("\u0414\u043E\u0431\u0430\u0432\u0438\u0442\u044C \u043F\u0430\u043F\u043A\u0443");
		addFolderButton.setBounds(434, 11, 283, 23);
		addFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jf = new JFileChooser();
				jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jf.setAcceptAllFileFilterUsed(false);
				jf.setDialogTitle("Выберите папку");
				jf.showOpenDialog(null);
				String pathToDir = jf.getSelectedFile().getAbsolutePath();
				for(final File fileEntry : new File(pathToDir).listFiles())
					if(!fileEntry.getName().equals("compress")) {
						if(fileEntry.isDirectory())
							getInsideFolder(pathToDir, fileEntry.getAbsolutePath());
						else { 
							getInsideFolder(pathToDir, fileEntry.getParent());
							break;
						}
					}
				if(count > 0) {
					Frame.button_3.setEnabled(true);
					clearListButton.setEnabled(true);
					updateAfterSize();
				} else JOptionPane.showMessageDialog(null, "В папке не найдено ни одного изображения", "Ошибка", JOptionPane.ERROR_MESSAGE);
			}
		});
		contentPane.add(addFolderButton);
		
		button_3 = new JButton("\u041D\u0430\u0447\u0430\u0442\u044C \u0441\u0436\u0430\u0442\u0438\u0435");
		button_3.setEnabled(false);
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Frame.button_3.setEnabled(false);
					Frame.progressBar.setValue(0);
					Frame.addFolderButton.setEnabled(false);
					Frame.clearListButton.setEnabled(false);
					Frame.textField.setEnabled(false);
					Frame.delButton.setEnabled(false);
					Frame.addButton.setEnabled(false);
					Information.compress(textField.getText());
				} catch (IOException | InterruptedException e) { }
			}
		});
		button_3.setBounds(435, 113, 282, 23);
		contentPane.add(button_3);
		
		totalFilesLabel = new JLabel("\u0412\u0441\u0435\u0433\u043E \u0444\u0430\u0439\u043B\u043E\u0432:");
		totalFilesLabel.setBounds(434, 146, 283, 14);
		contentPane.add(totalFilesLabel);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 413, 149);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 413, 149);
		panel.add(scrollPane);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		table = new JTable(model);
		scrollPane.setViewportView(table);
		table.getColumnModel().getColumn(0).setMaxWidth(30);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	if(table.getSelectedRow() != -1 && !clearing) {
		        	previewButton.setEnabled(true);
		            previewFileName = table.getValueAt(table.getSelectedRow(), 1).toString();
		            delButton.setEnabled(true);
	        	}
	        }
	    });
		
		table.removeColumn(table.getColumnModel().getColumn(3));
		
		JLabel label_1 = new JLabel("\u0421\u0436\u0430\u0442\u044C:");
		label_1.setBounds(727, 48, 49, 14);
		contentPane.add(label_1);
		
		textField = new JTextField();
		textField.setText("80");
		textField.setBounds(774, 45, 37, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		
		textField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateAfterSize();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				
			}
		});
		
		JLabel label_2 = new JLabel("%");
		label_2.setBounds(821, 48, 25, 14);
		contentPane.add(label_2);
		
		JLabel label_3 = new JLabel("\u0420\u0430\u0437\u043C\u0435\u0440 \u043F\u043E\u0441\u043B\u0435 \u0441\u0436\u0430\u0442\u0438\u044F:");
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 9));
		label_3.setBounds(727, 73, 115, 14);
		contentPane.add(label_3);
		
		sizeAfterLabel = new JLabel("...");
		sizeAfterLabel.setFont(new Font("Tahoma", Font.PLAIN, 9));
		sizeAfterLabel.setBounds(730, 87, 112, 14);
		contentPane.add(sizeAfterLabel);
		
		progressBar = new JProgressBar();
		progressBar.setForeground(Color.GREEN);
		progressBar.setStringPainted(true);
		progressBar.setBounds(722, 114, 106, 23);
		contentPane.add(progressBar);
		
		totalCompressed = new JLabel("...");
		totalCompressed.setHorizontalAlignment(SwingConstants.CENTER);
		totalCompressed.setBounds(722, 143, 108, 14);
		contentPane.add(totalCompressed);
		
		clearListButton = new JButton("\u041E\u0447\u0438\u0441\u0442\u0438\u0442\u044C");
		clearListButton.setEnabled(false);
		clearListButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearList();
			}
		});
		clearListButton.setBounds(589, 79, 128, 23);
		contentPane.add(clearListButton);
		
		previewButton = new JButton("\u041F\u0440\u0435\u0434\u043F\u0440\u043E\u0441\u043C\u043E\u0442\u0440");
		previewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = (String) Frame.table.getModel().getValueAt(Frame.table.getSelectedRow(), 3);
				String filename = (String) Frame.table.getModel().getValueAt(Frame.table.getSelectedRow(), 1);
				int rand = (int) (Math.random() * ( 9999 - 1 ));
				int randCmp = (int) (Math.random() * ( 9999 - 1 ));
				if(Information.getFl() != 0.0f) {
		            try { Information.compressFile(new File(path + "\\" + filename), path + "\\" + rand + "previewPRPR.jpg", Information.getFl(), randCmp); } catch (IOException e1) {}
		            new Preview(rand, randCmp, path);
				} else JOptionPane.showMessageDialog(null, "Процент сжатия должен быть в районе от 10 до 99 включительно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
			}
		});
		previewButton.setBounds(434, 79, 151, 23);
		contentPane.add(previewButton);
		previewButton.setEnabled(false);
		
		addButton = new JButton("\u0414\u043E\u0431\u0430\u0432\u0438\u0442\u044C \u0444\u0430\u0439\u043B");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jf = new JFileChooser();
				jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jf.setFileFilter(new FileNameExtensionFilter("Images only", "JPG", "jpg", "JPEG", "jpeg", "GIF", "gif", "BMP", "bmp", "TIF", "tif", "TIFF", "tiff", "PNG", "png", "WBMP", "wbmp"));
				jf.setAcceptAllFileFilterUsed(false);
				jf.setDialogTitle("Выберите папку, в которую сохранять сжатые файлы");
				jf.showOpenDialog(null);
				String pathToDir = jf.getSelectedFile().getParent();
				model.addRow(new String[] {Integer.toString(count + 1), jf.getSelectedFile().getName(), Long.toString(jf.getSelectedFile().length() / 1024) + " КБ", pathToDir });
				count++;
				lenght += jf.getSelectedFile().length() / 1024;
				updateAfterSize();
				clearListButton.setEnabled(true);
				button_3.setEnabled(true);
			}
		});
		addButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		addButton.setBounds(434, 45, 151, 23);
		contentPane.add(addButton);
		
		delButton = new JButton("\u0423\u0434\u0430\u043B\u0438\u0442\u044C \u0444\u0430\u0439\u043B");
		delButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearing = true;
				delButton.setEnabled(false);
				lenght -= Double.parseDouble((String) table.getValueAt(table.getSelectedRow(), 2).toString().replace(" КБ", ""));
				model.removeRow(table.getSelectedRow());
				count--;
				previewButton.setEnabled(false);
				if(table.getRowCount() == 0) {
					button_3.setEnabled(false);
					clearListButton.setEnabled(false);
				}
				updateAfterSize();
				clearing = false;
			}
		});
		delButton.setEnabled(false);
		delButton.setBounds(589, 45, 128, 23);
		contentPane.add(delButton);
		
		setVisible(true);
	}
	
	static void clearList() {
		clearing = true;
		int rowCount = model.getRowCount();
		table.getSelectionModel().clearSelection();
		button_3.setEnabled(false);
		count = 0;
		lenght = 0;
		sizeAfterLabel.setText("...");
		totalFilesLabel.setText("Всего файлов:");
		totalCompressed.setText("...");
		previewButton.setEnabled(false);
		delButton.setEnabled(false);
		clearListButton.setEnabled(false);
		previewFileName = "";
		progressBar.setValue(0);
		delButton.setEnabled(false);
		for (int i = rowCount - 1; i >= 0; i--) model.removeRow(i);
		clearing = false;
	}
	
	void updateAfterSize() {
		if(count > 0) {
			Frame.totalFilesLabel.setText("Всего файлов: " + count + " (" + lenght + " КБ / " + Double.toString(lenght / 1024).substring(0, 5) + " МБ)");
			Frame.totalCompressed.setText("0 / " + count);
			double onePercent = lenght / 100;
			int compressPercent = Integer.parseInt(textField.getText());
			int compressPercentSub = 100 - compressPercent;
			sizeAfterLabel.setText(Double.toString(onePercent * compressPercentSub).substring(0, 5) + " КБ / " + Double.toString(onePercent * compressPercentSub / 1024).substring(0, 5) + " МБ");
		} else {
			button_3.setEnabled(false);
			count = 0;
			lenght = 0;
			sizeAfterLabel.setText("...");
			totalFilesLabel.setText("Всего файлов:");
			totalCompressed.setText("...");
			previewButton.setEnabled(false);
			delButton.setEnabled(false);
			clearListButton.setEnabled(false);
			previewFileName = "";
			progressBar.setValue(0);
			delButton.setEnabled(false);
		}
	}
	
	void getInsideFolder(String pathToDir, String folderPath) {
		for(final File fileEntry : new File(folderPath).listFiles()) {
			String fileName = fileEntry.getName().toLowerCase();
			//"bmp", "jpg", "jpeg", "wbmp", "gif", "png", "tif"
			if((fileName.contains(".bmp") || fileName.contains(".jpg") || fileName.contains(".jpeg") || fileName.contains(".wbmp") ||
					fileName.contains(".gif") || fileName.contains(".png") || fileName.contains(".tif")) && !fileName.contains("previewPRPR.jpg")) {
				boolean findSame = false;
				for(int i = 0; i < Frame.count; i++) {
					if(Frame.table.getModel().getValueAt(i, 3).equals(fileEntry.getParent()) && Frame.table.getModel().getValueAt(i, 1).equals(fileEntry.getName())) {
						findSame = true;
						break;
					}
				}
				if(!findSame) {
					model.addRow(new String[] {Integer.toString(count + 1), fileEntry.getName(), Long.toString(fileEntry.length() / 1024) + " КБ", fileEntry.getParent() });
					lenght += fileEntry.length() / 1024;
					count++;
				}
			}
			if(fileEntry.isDirectory() && !fileEntry.getName().equals("compress")) getInsideFolder(pathToDir, fileEntry.getAbsolutePath());
		}
	}
}
