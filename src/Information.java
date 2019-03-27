import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JOptionPane;


public abstract class Information {
	
	public static Float getFl() {
		int q = Integer.parseInt(Frame.textField.getText());
		if(q >= 10 && q <= 99) {
			if(q > 90 && q <= 99) {
				String quality = "0.0" + (100 - q) + "f";
				Float fl = Float.parseFloat(quality);
				return fl;
			} else {
				String quality = "0." + (100 - q) + "f";
				Float fl = Float.parseFloat(quality);
				return fl;
			}
		} else return 0.0f;
		
	}
	
	public static void compress(String quality) throws IOException, InterruptedException {
		
		
		Frame.progressBar.setMaximum(Frame.count);
		Float fl = getFl();
		if(fl != 0.0f) {
			Thread tr = new Thread() {
				@Override
				public void run() {
					int i = 0;
					for(int j = 0; j < Frame.count; j++) {
						File f = new File(Frame.table.getModel().getValueAt(j, 3) + "\\compress");
						File orig = new File(Frame.table.getModel().getValueAt(j, 3) + "\\" + Frame.table.getModel().getValueAt(j, 1));
						try {
							if(f.mkdir()) {}
							compressFile(orig, Frame.table.getModel().getValueAt(j, 3) + "\\compress\\" + Frame.table.getModel().getValueAt(j, 1), fl, (int) (Math.random() * ( 9999 - 1 )));
							Frame.totalCompressed.setText((i += 1) + " / " + Frame.count);
							Frame.progressBar.setValue(Frame.progressBar.getValue() + 1);
						} catch(IllegalArgumentException | IOException e) {
							e.printStackTrace();
						}
					}
					int result = JOptionPane.showConfirmDialog(null, "Удалить оригиналы?", "Подтверждение", JOptionPane.OK_CANCEL_OPTION);
					String dels = "Удаление оригиналов: нет";
					int delsI = 0;
					if(result == 0) {
						for(int j = 0; j < Frame.count; j++) {
							File orig = new File(Frame.table.getModel().getValueAt(j, 3) + "\\" + Frame.table.getModel().getValueAt(j, 1));
							orig.delete();
							delsI++;
						}
						dels = "Удаление оригиналов: " + delsI;
					} else {
						Frame.button_3.setEnabled(true);
						Frame.delButton.setEnabled(true);
					}
					int zipConfirm = JOptionPane.showConfirmDialog(null, "Поместить сжатые файлы в архив, чтобы сэкономить ещё больше места?", "Подтверждение", JOptionPane.OK_CANCEL_OPTION);
					String zipMoveLabel = "Разместить по архивам: нет";
					if(zipConfirm == 0) {
						Frame.progressBar.setValue(0);
						String oldFolderZip = "";
						for(int j = 0; j < Frame.count; j++) {
							if(!(Frame.table.getModel().getValueAt(j, 3) + "\\compress").equals(oldFolderZip)) {
								oldFolderZip = Frame.table.getModel().getValueAt(j, 3) + "\\compress";
								File zip = new File(oldFolderZip + "\\compress.zip");
								if(!zip.exists()) {
									try { 
										zip.createNewFile();
										OutputStream stream = new FileOutputStream(zip);
								        stream = new BufferedOutputStream(stream);
								        ZipOutputStream zipStream = new ZipOutputStream(stream);
								        zipStream.close();
									} catch (IOException e1) {e1.printStackTrace();}
								} else unZipIt(oldFolderZip + "\\compress.zip", oldFolderZip);
								
								ZipOutputStream zipOut = null;
								try { zipOut = new ZipOutputStream(new FileOutputStream(zip));
								} catch (FileNotFoundException e) { e.printStackTrace(); }
								
								for(File f : new File(oldFolderZip).listFiles())
									if(!f.getName().contains("compress.zip"))
										try {
											zipFile(zipOut, "", f);
											f.delete();
										} catch (IOException e) { e.printStackTrace(); }
								try {
									zipOut.close();
								} catch (IOException e) { e.printStackTrace(); }
								zipMoveLabel = "Разместить по архивам: да";
							}
							Frame.progressBar.setValue(Frame.progressBar.getValue() + 1);
						} Frame.progressBar.setValue(0);
						
					} else {
						Frame.button_3.setEnabled(true);
						Frame.delButton.setEnabled(true);
					}
					double length = 0.0;
					String oldFolder = "";
					for(int j = 0; j < Frame.count; j++) {
						if(!(Frame.table.getModel().getValueAt(j, 3) + "\\compress").equals(oldFolder)) {
							oldFolder = Frame.table.getModel().getValueAt(j, 3) + "\\compress";
							for(File fileEntry : new File(oldFolder).listFiles()) length += fileEntry.length();
						}
					}
					String origReport = Double.toString(Frame.lenght / 1024).substring(0, 5);
					String comprReport = Double.toString(length / 1024 / 1024).substring(0, 5);
					String economy = Double.toString((Frame.lenght / 1024) / (length / 1024 / 1024)).substring(0, 4);
					JOptionPane.showMessageDialog(null, "Программа закончила свою работу с результатом:\nОригинал: " + origReport + " МБ\nРезультат сжатия: " + comprReport + " МБ\nЭкономия места в " + economy + " раз!\n" + dels + "\n" + zipMoveLabel, "Готово!", JOptionPane.INFORMATION_MESSAGE);
					Frame.clearList();
					Frame.addFolderButton.setEnabled(true);
					Frame.textField.setEnabled(true);
					Frame.addButton.setEnabled(true);
				}
			};
			tr.start();
		} else { 
			Frame.button_3.setEnabled(true);
			Frame.progressBar.setValue(0);
			Frame.addFolderButton.setEnabled(true);
			Frame.clearListButton.setEnabled(true);
			Frame.textField.setEnabled(true);
			Frame.delButton.setEnabled(true);
			Frame.addButton.setEnabled(true);
			JOptionPane.showMessageDialog(null, "Процент сжатия должен быть в районе от 10 до 99 включительно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void unZipIt(String zipFile, String outputFolder){

	     byte[] buffer = new byte[1024];
	    	
	     try{
	    	 
	    	File folder = new File(outputFolder);
	    	if(!folder.exists()){
	    		folder.mkdir();
	    	}
	    		
	    	ZipInputStream zis = 
	    		new ZipInputStream(new FileInputStream(zipFile));
	    	ZipEntry ze = zis.getNextEntry();
	    		
	    	while(ze!=null){
	    			
	    	   String fileName = ze.getName();
	           File newFile = new File(outputFolder + File.separator + fileName);
	                
	            new File(newFile.getParent()).mkdirs();
	              
	            FileOutputStream fos = new FileOutputStream(newFile);             

	            int len;
	            while ((len = zis.read(buffer)) > 0) {
	       		fos.write(buffer, 0, len);
	            }
	        		
	            fos.close();   
	            ze = zis.getNextEntry();
	    	}
	    	
	        zis.closeEntry();
	    	zis.close();
	    		
	    }catch(IOException ex){
	       ex.printStackTrace(); 
	    }
	   }    
	
	private static String buildPath(String path, String file)
    {
        if (path == null || path.isEmpty())
        {
            return file;
        } else
        {
            return path + "/" + file;
        }
    }
	
	private static void zipFile(ZipOutputStream zos, String path, File file) throws IOException
    {
        if (!file.canRead())
        {
            return;
        }

        zos.putNextEntry(new ZipEntry(buildPath(path, file.getName())));

        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[4092];
        int byteCount = 0;
        while ((byteCount = fis.read(buffer)) != -1)
        {
            zos.write(buffer, 0, byteCount);
            System.out.print('.');
            System.out.flush();
        }

        fis.close();
        zos.closeEntry();
    }
	
	static void compressFile(File fileEntry, String compPath, Float fl, int randCmp) throws IOException {
		BufferedImage image = ImageIO.read(fileEntry);
		File compressedImageFile = new File(compPath + "_cmp" + randCmp + ".jpg");
		OutputStream os = new FileOutputStream(compressedImageFile);
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
	    ImageWriter writer = (ImageWriter) writers.next();
	    ImageOutputStream ios = ImageIO.createImageOutputStream(os);
	    writer.setOutput(ios);
	    ImageWriteParam param = writer.getDefaultWriteParam();
	    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	    param.setCompressionQuality(fl);
	    writer.write(null, new IIOImage(image, null, null), param);
	    os.close();
	    ios.close();
	    writer.dispose();
	}
}
