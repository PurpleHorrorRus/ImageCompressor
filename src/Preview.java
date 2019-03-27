import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Preview extends JFrame {

	private static final long serialVersionUID = -2892209821937977915L;
	private JPanel contentPane;
	File origFile, prevFile;
	int rand = 0;
	public Preview(int random, int randCmp, String path) {
		setResizable(false);
		this.rand = random;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				File del = new File(path + "\\" + rand + "previewPRPR.jpg_cmp" + randCmp + ".jpg");
				del.delete();
			}
		});
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 1504, 740);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		setTitle("Preview for " + Frame.previewFileName);
				
		BufferedImage prev = null, orig = null;
		try {
			origFile = new File(path + "\\" + Frame.table.getModel().getValueAt(Frame.table.getSelectedRow(), 1));
			prevFile = new File(path + "\\" + this.rand + "previewPRPR.jpg_cmp" + randCmp + ".jpg");
			orig = ImageIO.read(origFile);
		    prev = ImageIO.read(prevFile);	    
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		JLabel lblOrigsize = new JLabel(origFile.length() / 1024 + " สม");
		lblOrigsize.setBounds(338, 11, 46, 14);
		contentPane.add(lblOrigsize);
		
		JLabel lblPrevsize = new JLabel(prevFile.length() / 1024 + " สม");
		lblPrevsize.setBounds(1109, 11, 46, 14);
		contentPane.add(lblPrevsize);
		
		JLabel label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(780, 11, 698, 679);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("");
		label_1.setBounds(10, 11, 698, 679);
		contentPane.add(label_1);
		
		setVisible(true);
		
		Image dimg = prev.getScaledInstance(label.getWidth(), label.getHeight(),
		        Image.SCALE_SMOOTH);
		
		Image dimg1 = orig.getScaledInstance(label_1.getWidth(), label_1.getHeight(),
		        Image.SCALE_SMOOTH);
		
		ImageIcon imageIcon = new ImageIcon(dimg);
		label.setIcon(imageIcon);
		
		ImageIcon imageIcon1 = new ImageIcon(dimg1);
		label_1.setIcon(imageIcon1);
		
		JLabel label_2 = new JLabel("->");
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setBounds(718, 339, 46, 40);
		contentPane.add(label_2);
		
		JLabel label_3 = new JLabel(Frame.textField.getText() + "%");
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 16));
		label_3.setHorizontalAlignment(SwingConstants.CENTER);
		label_3.setBounds(718, 323, 46, 29);
		contentPane.add(label_3);
		
	}
	
	int getRandom() { return this.rand; }
}
