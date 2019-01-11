import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
//import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.sourceforge.jFuzzyLogic.FIS;

public class ImgTool extends JFrame implements ActionListener {
	
	public static float[] rgbToHsv(int r, int g, int b) {
		float r1=(float)r/255, g1=(float)g/255, b1=(float)b/255;
		float max = Math.max(Math.max(r1, g1), b1);
		float min = Math.min(Math.min(r1, g1), b1);
		float h=max, s=max, v=max;
		float d = max - min;
		s = max == 0 ? 0 : d / max;
		if (max == min) {
			h = 0; // achromatic
		}
		else {
			if(max==r1)
				h = (g1 - b1) / d + (g1 < b1 ? 6 : 0);
			else if(max==g1)
				h = (b1 - r1) / d + 2;
			else
				h = (r1 - g1) / d + 4;
			h /= 6;
		}
		float[] hsv = new float[3];
		hsv[0]=h; hsv[1]=s; hsv[2]=v;
		return hsv;
	}
	
	public static int[] hsvToRgb(float h, float s, float v) {
		float r=0, g=0, b=0;
		float i = (float) Math.floor(h * 6);
		float f = h * 6 - i;
		float p = v * (1 - s);
		float q = v * (1 - f * s);
		float t = v * (1 - (1 - f) * s);

		switch((int)(i%6)) {
			case 0: r = v; g = t; b = p; break;
			case 1: r = q; g = v; b = p; break;
			case 2: r = p; g = v; b = t; break;
			case 3: r = p; g = q; b = v; break;
			case 4: r = t; g = p; b = v; break;
			case 5: r = v; g = p; b = q; break;
		}
		int[] rgb = new int[3];
		rgb[0]=(int)(r*255); rgb[1]=(int)(g*255); rgb[2]=(int)(b*255);
		return rgb;
	}
	int edit_flag = 0;
	BufferedImage img, newImage;
	WritableRaster raster;
	JMenuBar mb;
	JMenu file, edit;
	JMenuItem open, save, contrast, color_contrast, greyscale, edge_detect, segment;
	ImageIcon icon = new ImageIcon("src\\black.png");
	JLabel label = new JLabel(icon);
	JScrollPane scroll = new JScrollPane(label);
	ImgTool(){
		open = new JMenuItem("Open File");
		open.addActionListener(this);
		save = new JMenuItem("Save File As");
		save.addActionListener(this);
		file = new JMenu("File");
		file.add(open);
		file.add(save);
		greyscale = new JMenuItem("Convert to Greyscale");
		greyscale.addActionListener(this);
		contrast = new JMenuItem("Increase Greyscale Contrast");
		contrast.addActionListener(this);
		color_contrast = new JMenuItem("Increase Color Contrast");
		color_contrast.addActionListener(this); 
		edge_detect = new JMenuItem("Show edges");
		edge_detect.addActionListener(this);
		segment = new JMenuItem("Show segments");
		segment.addActionListener(this); 
		edit = new JMenu("Edit");
		edit.add(greyscale);
		edit.add(contrast);
		edit.add(color_contrast);
		edit.add(edge_detect);
		edit.add(segment);
		mb = new JMenuBar();
		mb.setBounds(0, 0, 800, 20);
		mb.add(file);
		mb.add(edit);
		//add(mb); commented out because of vertical display
		this.setJMenuBar(mb);
		setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == open) {
			JFileChooser fc = new JFileChooser();
			int i = fc.showOpenDialog(this);
			if(i == JFileChooser.APPROVE_OPTION) {
				File imagefile = fc.getSelectedFile();
				String filepath = imagefile.getPath();
				try {
					remove(scroll);
					img = ImageIO.read(new File(filepath));
					icon = new ImageIcon(img);
					label = new JLabel(icon);
					scroll = new JScrollPane(label);
					add(scroll);
					pack();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			edit_flag = 1;
		}
		if(e.getSource() == save) {
			try {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Specify a file to save");
				int userSelection = fc.showSaveDialog(null);
				if (userSelection == JFileChooser.APPROVE_OPTION) {
					File outputfile = new File(fc.getSelectedFile() + ".jpg");
					if(edit_flag == 0) {
						JOptionPane.showMessageDialog(null, "No image is opened.", "Cannot Save", JOptionPane.WARNING_MESSAGE);
					}
					else if(edit_flag == 1)
						ImageIO.write(img, "jpg", outputfile);
					else
						ImageIO.write(newImage, "jpg", outputfile);
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if(e.getSource() == contrast) {
			try {
				String fileName = "src\\contrastfuzzy.fcl";
		        FIS fis = FIS.load(fileName, true);
		        if( fis == null ) { 
		            System.err.println("Can't load file: '" + fileName + "'");
		            return;
		        }
		        newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		        //raster = newImage.getRaster();
		        //int[] pixels = new int[3];
		        int width = img.getWidth();
		        int height = img.getHeight();
				int grey, val, rgb;
				for(int p=0; p<width; p++) {
					for(int q=0; q<height; q++) {
						Color c = new Color(img.getRGB(p, q));
						grey = (int) (0.299*c.getRed() + 0.587*c.getGreen() + 0.114*c.getBlue());
						fis.setVariable("inpixel", grey);
						fis.evaluate();
						val = (int) fis.getVariable("outpixel").getValue();
						rgb = new Color(val, val, val).getRGB();
						newImage.setRGB(p, q, rgb);
						//pixels[0]=val; pixels[1]=val; pixels[2]=val;
						//raster.setPixel(p, q, pixels);
					}
				}
				//newImage.setData(raster);
				remove(scroll);
				icon = new ImageIcon(newImage);
				label = new JLabel(icon);
				scroll = new JScrollPane(label);
				add(scroll);
				pack();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			edit_flag = 2;
		}
		if(e.getSource() == color_contrast) {
			try {
				String fileName = "src\\cc.fcl";
		        FIS fis = FIS.load(fileName, true);
		        if( fis == null ) { 
		            System.err.println("Can't load file: '" + fileName + "'");
		            return;
		        }
		        newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		        int width = img.getWidth();
		        int height = img.getHeight();
				int[] rgb = new int[3];
				float[] hsv = new float[3];
				int rgbcolor;
				for(int p=0; p<width; p++) {
					for(int q=0; q<height; q++) {
						Color c = new Color(img.getRGB(p, q));
						rgb[0]=c.getRed(); rgb[1]=c.getGreen(); rgb[2]=c.getBlue();
						hsv = rgbToHsv(rgb[0], rgb[1], rgb[2]);
						//System.out.println(hsv[2]);
						fis.setVariable("invib", hsv[2]);
						fis.evaluate();
						hsv[2] = (float) fis.getVariable("outvib").getValue();
						//System.out.println(hsv[2]);
						rgb = hsvToRgb(hsv[0], hsv[1], hsv[2]);
						rgbcolor = new Color(rgb[0], rgb[1], rgb[2]).getRGB();
						newImage.setRGB(p, q, rgbcolor);
						//System.out.println(rgb[0] + "," + rgb[0]+ "," + rgb[2]);
					}
				}
				remove(scroll);
				icon = new ImageIcon(newImage);
				label = new JLabel(icon);
				scroll = new JScrollPane(label);
				add(scroll);
				pack();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			edit_flag = 2;
		}
		if(e.getSource() == greyscale) {
			try {
		        newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		        int width = img.getWidth();
		        int height = img.getHeight();
				int grey, rgb;
				for(int p=0; p<width; p++) {
					for(int q=0; q<height; q++) {
						Color c = new Color(img.getRGB(p, q));
						grey = (int) (0.299*c.getRed() + 0.587*c.getGreen() + 0.114*c.getBlue());
						rgb = new Color(grey, grey, grey).getRGB();
						newImage.setRGB(p, q, rgb);
					}
				}
				remove(scroll);
				icon = new ImageIcon(newImage);
				label = new JLabel(icon);
				scroll = new JScrollPane(label);
				add(scroll);
				pack();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			edit_flag = 2;
		}
		
		if(e.getSource() == edge_detect) {
			try {
				String fileName = "src\\edge.fcl";
		        FIS fis = FIS.load(fileName, true);
		        if( fis == null ) { 
		            System.err.println("Can't load file: '" + fileName + "'");
		            return;
		        }
		        newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		        int width = img.getWidth();
		        int height = img.getHeight();
				int[] pix = new int[9];
				int rgbcolor, grey;
				Color c;
				for(int p=0; p<width; p++) {
					for(int q=0; q<height; q++) {
						c = new Color(img.getRGB(p, q));
						grey = (int) (0.299*c.getRed() + 0.587*c.getGreen() + 0.114*c.getBlue());
						rgbcolor = new Color(grey, grey, grey).getRGB();
						newImage.setRGB(p, q, rgbcolor);
					}
				}
				for(int p=0; p<=width-3; p++) {
					for(int q=0; q<=height-3; q++) {
						pix[0] = new Color(img.getRGB(p+1, q+1)).getRed();
						
						pix[1] = new Color(img.getRGB(p, q)).getRed() - pix[0];
						fis.setVariable("inpixel", pix[1]);
						fis.evaluate();
						pix[1] = (int) fis.getVariable("outpixel").getValue();
						newImage.setRGB(p, q, new Color(pix[1], pix[1], pix[1]).getRGB());
						
						pix[2] = new Color(img.getRGB(p, q+1)).getRed() - pix[0];
						fis.setVariable("inpixel", pix[2]);
						fis.evaluate();
						pix[2] = (int) fis.getVariable("outpixel").getValue();
						newImage.setRGB(p, q, new Color(pix[2], pix[2], pix[2]).getRGB());
						
						pix[3] = new Color(img.getRGB(p, q+2)).getRed() - pix[0];
						fis.setVariable("inpixel", pix[3]);
						fis.evaluate();
						pix[3] = (int) fis.getVariable("outpixel").getValue();
						newImage.setRGB(p, q, new Color(pix[3], pix[3], pix[3]).getRGB());
						
						pix[4] = new Color(img.getRGB(p+1, q)).getRed() - pix[0];
						fis.setVariable("inpixel", pix[4]);
						fis.evaluate();
						pix[4] = (int) fis.getVariable("outpixel").getValue();
						newImage.setRGB(p, q, new Color(pix[4], pix[4], pix[4]).getRGB());
						
						pix[5] = new Color(img.getRGB(p+1, q+2)).getRed() - pix[0];
						fis.setVariable("inpixel", pix[5]);
						fis.evaluate();
						pix[5] = (int) fis.getVariable("outpixel").getValue();
						newImage.setRGB(p, q, new Color(pix[5], pix[5], pix[5]).getRGB());
						
						pix[6] = new Color(img.getRGB(p+2, q)).getRed() - pix[0];
						fis.setVariable("inpixel", pix[6]);
						fis.evaluate();
						pix[6] = (int) fis.getVariable("outpixel").getValue();
						newImage.setRGB(p, q, new Color(pix[6], pix[6], pix[6]).getRGB());
						
						pix[7] = new Color(img.getRGB(p+2, q+1)).getRed() - pix[0];
						fis.setVariable("inpixel", pix[7]);
						fis.evaluate();
						pix[7] = (int) fis.getVariable("outpixel").getValue();
						newImage.setRGB(p, q, new Color(pix[7], pix[7], pix[7]).getRGB());
						
						pix[8] = new Color(img.getRGB(p+2, q+2)).getRed() - pix[0];
						fis.setVariable("inpixel", pix[8]);
						fis.evaluate();
						pix[8] = (int) fis.getVariable("outpixel").getValue();
						newImage.setRGB(p, q, new Color(pix[8], pix[8], pix[8]).getRGB());
					}
				}
				remove(scroll);
				icon = new ImageIcon(newImage);
				label = new JLabel(icon);
				scroll = new JScrollPane(label);
				add(scroll);
				pack();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			edit_flag = 2;
		}

		if(e.getSource() == segment) {
			try {
				String fileName = "src\\segment.fcl";
		        FIS fis = FIS.load(fileName, true);
		        if( fis == null ) { 
		            System.err.println("Can't load file: '" + fileName + "'");
		            return;
		        }
		        newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		        raster = newImage.getRaster();
		        int[] pixels = new int[3];
		        int width = img.getWidth();
		        int height = img.getHeight();
		        double r1, g1, b1;
				for(int p=0; p<width; p++) {
					for(int q=0; q<height; q++) {
						Color c = new Color(img.getRGB(p, q));
						r1 = 0.257*c.getRed()+0.504*c.getGreen()+0.979*c.getBlue()+16;
						g1 = -0.148*c.getRed()-0.291*c.getGreen()+0.439*c.getBlue()+128;
						b1 = 0.439*c.getRed()-0.368*c.getGreen()-0.071*c.getBlue()+128;
						fis.setVariable("y", r1);
						fis.setVariable("cb", g1);
						fis.setVariable("cr", b1);
						fis.evaluate();
						pixels[0] = (int) fis.getVariable("outred").getValue();
						pixels[1] = (int) fis.getVariable("outgreen").getValue();
						pixels[2] = (int) fis.getVariable("outblue").getValue();
						raster.setPixel(p, q, pixels);
					}
				}
				newImage.setData(raster);
				remove(scroll);
				icon = new ImageIcon(newImage);
				label = new JLabel(icon);
				scroll = new JScrollPane(label);
				add(scroll);
				pack();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			edit_flag = 2;
		}
	}
	
	public static void main(String[] args) {
		new ImgTool();
	}
}