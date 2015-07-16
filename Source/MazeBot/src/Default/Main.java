package Default;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JCheckBox;

public class Main {

	public static JFrame parentFrame;
	private static JButton walkB, recurB, saveB;
	public static BufferedImage map = null, overlay = null;
	public static JLabel Canvas;
	public static int h, w;
	public static JCheckBox trail, inf;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		h = 0;
		w = 0;

		// Frame Setup
		JFrame frame = new JFrame("MazeBot");
		JPanel mainPanel = new JPanel(new BorderLayout()), buttonPanel = new JPanel(), canvasPanel = new JPanel();
		JButton openMapButton = new JButton(), walkButton = new JButton(), recurButton = new JButton(), mapButton = new JButton(), saveMapButton = new JButton();
		JLabel canvas = new JLabel();
		JCheckBox displayTrail = new JCheckBox(), infChk = new JCheckBox();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setPreferredSize(new Dimension(1000, 700));
		openMapButton.setPreferredSize(new Dimension(100, 30));
		walkButton.setPreferredSize(new Dimension(100, 30));
		recurButton.setPreferredSize(new Dimension(100, 30));
		mapButton.setPreferredSize(new Dimension(100, 30));
		saveMapButton.setPreferredSize(new Dimension(100, 30));
		openMapButton.setText("Open Map");
		walkButton.setText("Walk");
		recurButton.setText("Pathfinding");
		displayTrail.setText("Trail");
		infChk.setText("inf");
		mapButton.setText("Generate Map");
		saveMapButton.setText("Save Map");
		walkButton.setEnabled(false);
		recurButton.setEnabled(false);
		saveMapButton.setEnabled(false);
		buttonPanel.add(openMapButton);
		buttonPanel.add(walkButton);
		buttonPanel.add(recurButton);
		buttonPanel.add(mapButton);
		buttonPanel.add(displayTrail);
		// buttonPanel.add(infChk);
		canvasPanel.add(canvas);
		mainPanel.add(canvasPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		mainPanel.add(new JPanel(new BorderLayout()).add(saveMapButton), BorderLayout.SOUTH);
		frame.setContentPane(mainPanel);
		frame.pack();
		// End Frame Setup
		walkB = walkButton;
		recurB = recurButton;
		saveB = saveMapButton;
		parentFrame = frame;
		Canvas = canvas;
		trail = displayTrail;
		inf = infChk;
		openMapButton.addActionListener(new LoadImage());
		walkButton.addActionListener(new AI());
		walkButton.setActionCommand("walk");
		recurButton.addActionListener(new AI());
		recurButton.setActionCommand("recur");
		mapButton.addActionListener(new MapGen());
		saveMapButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileDialog fd;
				String fileName;
				fd = new FileDialog(Main.parentFrame, "Open");
				fd.setMultipleMode(false);
				fd.setDirectory("C:\\");
				fd.setVisible(true);
				fileName = fd.getDirectory() + fd.getFile();
				File file = new File(fileName + ".bmp");
				try {
					ImageIO.write(map, "bmp", file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
	}

	public static void checkMapBuffered() {
		if (map == null) {
			walkB.setEnabled(false);
			recurB.setEnabled(false);
			saveB.setEnabled(false);
			Canvas.setIcon(new ImageIcon());
		} else {
			walkB.setEnabled(true);
			recurB.setEnabled(true);
			saveB.setEnabled(true);
			h = map.getHeight();
			w = map.getWidth();
		}
	}

}
