package Default;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;

public class AI implements ActionListener {

	int startCoords[], endCoords[], computeTrail[][];
	long startTime, endTime;
	int h, w;
	BufferedImage map, overlay;
	String mode;
	static runAI inst;
	node activeNode;

	public AI(String command) {
		startCoords = new int[2];
		startCoords[0] = 0;
		startCoords[1] = 0;
		endCoords = new int[2];
		endCoords[0] = 0;
		endCoords[1] = 0;
		mode = command;
		map = Main.map;
		h = map.getHeight();
		w = map.getWidth();
		computeTrail = new int[w][h];
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
				if (map.getRGB(i, j) == Color.black.getRGB())
					computeTrail[i][j] = 100;
				else
					computeTrail[i][j] = 0;
		inst = new runAI(command);
		Boolean gotStart = false, gotEnd = false;
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				Color color = new Color(map.getRGB(x, y), true);
				// JOptionPane.showMessageDialog(Main.parentFrame,
				// color.toString());
				if (color.getRed() == 255 && color.getBlue() == 0 && !gotStart) {
					startCoords[0] = x;
					startCoords[1] = y;
					gotStart = true;
				} else if (color.getBlue() == 255 && color.getRed() == 0
						&& !gotEnd) {
					endCoords[0] = x;
					endCoords[1] = y;
					gotEnd = true;
				}
			}
		}
		if (startCoords[0] == endCoords[0] && startCoords[1] == endCoords[1])
			JOptionPane.showMessageDialog(Main.parentFrame, "Invalid Image");
		else
			Run();
	}

	public AI() {

	}

	private void Run() {

		if (mode == "walk") {
			JOptionPane
					.showMessageDialog(Main.parentFrame,
							"AI Initialized Successfully, Will Now Commence 'Stupid Human' Type Mapping");
			startTime = System.currentTimeMillis();
			inst.execute();
		} else if (mode == "recur") {
			inst.execute();
		} else
			JOptionPane.showMessageDialog(Main.parentFrame,
					"Something went wrong (AI mode not recognized)");
	}

	public void actionPerformed(ActionEvent e) {
		String comm = e.getActionCommand();
		new AI(comm);

	}

	private void walk() {
		int speedX = 1, speedY = 0, posX = startCoords[0], posY = startCoords[1];
		while (map.getRGB(posX, posY) != Color.blue.getRGB()) {

			// Thread.sleep(100);
			computeTrail[posX][posY] += 1;
			if (map.getRGB(posX - speedX, posY - speedY) != Color.black
					.getRGB()) {
				map.setRGB(posX - speedX, posY - speedY, Color.white.getRGB());
			}
			if (Main.trail.isSelected()) {
				for (int x = 0; x < w; x++)
					for (int y = 0; y < h; y++)
						if (map.getRGB(x, y) != Color.black.getRGB() && computeTrail[x][y] > 0)
							map.setRGB(x, y, Color.yellow.getRGB());
			} else {
				for (int x = 0; x < w; x++)
					for (int y = 0; y < h; y++)
						if (map.getRGB(x, y) != Color.black.getRGB() && computeTrail[x][y] > 0)
							map.setRGB(x, y, Color.white.getRGB());
			}

			if (map.getRGB(posX + speedX, posY + speedY) == Color.black
					.getRGB()) {
				if (speedX != 0) {
					if (map.getRGB(posX, posY + 1) != Color.black.getRGB() && computeTrail[posX][posY + 1] < computeTrail[posX][posY - 1]) {
						speedX = 0;
						speedY = 1;
					} else if (map.getRGB(posX, posY - 1) != Color.black
							.getRGB()) {
						speedX = 0;
						speedY = -1;
					} else
						speedX = -speedX;
				} else {
					if (map.getRGB(posX + 1, posY) != Color.black.getRGB() && computeTrail[posX + 1][posY] < computeTrail[posX - 1][posY]) {
						speedX = 1;
						speedY = 0;
					} else if (map.getRGB(posX - 1, posY) != Color.black
							.getRGB()) {
						speedX = -1;
						speedY = 0;
					} else
						speedY = -speedY;
				}
			}

			else if (computeTrail[posX + speedX][posY + speedY] > 0) {
				if (computeTrail[posX + speedY][posY + speedX] < computeTrail[posX
						+ speedX][posY + speedY]) {
					int buff = speedX;
					speedX = speedY;
					speedY = buff;
				} else if (computeTrail[posX - speedY][posY - speedX] < computeTrail[posX
						+ speedX][posY + speedY]) {
					int buff = speedX;
					speedX = -speedY;
					speedY = -buff;
				}
			}
			map.setRGB(posX, posY, Color.green.getRGB());
			posX += speedX;
			posY += speedY;
			Image scaled = map.getScaledInstance(map.getWidth()
					* (Main.Canvas.getIcon().getIconWidth() / Main.w),
					map.getHeight()
							* (Main.Canvas.getIcon().getIconHeight() / Main.h),
					Image.SCALE_DEFAULT);
			Main.Canvas.setIcon(new ImageIcon(scaled));
		}
	}

	class runAI extends SwingWorker<Void, Void> {
		Boolean running;
		String algorithm;

		public runAI(String type) {
			algorithm = type;
		}

		@Override
		protected Void doInBackground() throws Exception {
			if (algorithm == "walk") {
				do {
					walk();
				} while (Main.inf.isSelected());
			}
			// ####################################################################################
			// ####################################################################################
			else {
				JPanel optionPanel = new JPanel(new GridLayout(0,1));
				ButtonGroup radios = new ButtonGroup();
				JRadioButton fastButton = new JRadioButton(), shortButton = new JRadioButton(); 
				fastButton.setText("Greedy A* (sometimes faster)");
				shortButton.setText("True A* (finds shortest route)");
				shortButton.setSelected(true);
				radios.add(shortButton);
				radios.add(fastButton);
				optionPanel.add(shortButton);
				optionPanel.add(fastButton);
				JOptionPane.showConfirmDialog(Main.parentFrame,
						optionPanel, "Map Options", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				Boolean shortest = shortButton.isSelected();
				
				//JOptionPane.showMessageDialog(Main.parentFrame, "short: " + shortest);
				startTime = System.currentTimeMillis();
				node nodes[][] = new node[w][h], startNode = null, endNode = null, closeNodes[] = new node[4], nextNode = null;
				for (int x = 0; x < w; x++)
					for (int y = 0; y < h; y++) { 
						if (startCoords[0] == x && startCoords[1] == y)
							startNode = nodes[x][y] = new node(x, y);
						else if (endCoords[0] == x && endCoords[1] == y)
							endNode = nodes[x][y] = new node(x, y);
						else if (map.getRGB(x, y) == Color.black.getRGB())
							nodes[x][y] = new node(x, y, true);
						else if (map.getRGB(x, y) == Color.white.getRGB())
							nodes[x][y] = new node(x, y);
					}
				activeNode = startNode;
				activeNode.G = 1;
				while (activeNode != endNode) {
					map.setRGB(activeNode.x, activeNode.y,
							Color.yellow.getRGB());
					if (!nodes[activeNode.x - 1][activeNode.y].closed
							&& !nodes[activeNode.x - 1][activeNode.y].isIllegal)
						closeNodes[0] = nodes[activeNode.x - 1][activeNode.y];
					else
						closeNodes[0] = null;
					if (!nodes[activeNode.x][activeNode.y - 1].closed
							&& !nodes[activeNode.x][activeNode.y - 1].isIllegal)
						closeNodes[1] = nodes[activeNode.x][activeNode.y - 1];
					else
						closeNodes[1] = null;
					if (!nodes[activeNode.x + 1][activeNode.y].closed
							&& !nodes[activeNode.x + 1][activeNode.y].isIllegal)
						closeNodes[2] = nodes[activeNode.x + 1][activeNode.y];
					else
						closeNodes[2] = null;
					if (!nodes[activeNode.x][activeNode.y + 1].closed
							&& !nodes[activeNode.x][activeNode.y + 1].isIllegal)
						closeNodes[3] = nodes[activeNode.x][activeNode.y + 1];
					else
						closeNodes[3] = null;
					nextNode = null;
					for (int j = 0; j < 4; j++)
						if (closeNodes[j] != null) {
							nextNode = closeNodes[j];
							// JOptionPane.showMessageDialog(Main.parentFrame,
							// "NextNode pos: " + nextNode.x + " " +
							// nextNode.y);
						}
					if (nextNode == null) {
						for(int x = 0; x < w; x++)
							for(int y = 0; y < h; y++)
								if(nodes[x][y].open)
									nextNode = nodes[x][y];
						// JOptionPane.showMessageDialog(Main.parentFrame,
						// "NextNode pos: " + nextNode.x + " " + nextNode.y);
					}
					for (int i = 0; i < 4; i++)
						if (closeNodes[i] != null) {
							if (activeNode == startNode) {
								closeNodes[i].G = activeNode.G + 1;
								closeNodes[i].parent = activeNode;
							} else if (closeNodes[i].G > activeNode.G + 1
									|| !closeNodes[i].open) {
								closeNodes[i].G = activeNode.G + 1;
								closeNodes[i].parent = activeNode;
							}
							closeNodes[i].open = true;
							closeNodes[i].F = closeNodes[i].G + closeNodes[i].H;
							if (closeNodes[i] == endNode)
								nextNode = endNode;
						}
					activeNode.closed = true;
					activeNode.open = false;
					if (nextNode != endNode)
						for (int x = 0; x < w; x++)
							for (int y = 0; y < h; y++)
								if (nodes[x][y].open) {
									if (nodes[x][y].F < nextNode.F && shortest)
										nextNode = nodes[x][y];
									else if (nodes[x][y].H < nextNode.H && !shortest)
										nextNode = nodes[x][y];
									map.setRGB(x, y, Color.green.getRGB());
								}

					// JOptionPane.showMessageDialog(Main.parentFrame, "nextX: "
					// + nextNode.x + " nextY: " + nextNode.y);
					if (Main.trail.isSelected()) {
						Image scaled = map
								.getScaledInstance(
										map.getWidth()
												* (Main.Canvas.getIcon()
														.getIconWidth() / Main.w),
										map.getHeight()
												* (Main.Canvas.getIcon()
														.getIconHeight() / Main.h),
										Image.SCALE_DEFAULT);
						Main.Canvas.setIcon(new ImageIcon(scaled));
					}
					activeNode = nextNode;
				}
				while (activeNode != startNode) {
					activeNode = activeNode.parent;
					map.setRGB(activeNode.x, activeNode.y, Color.red.getRGB());
					if (Main.trail.isSelected()) {
						Image scaled = map
								.getScaledInstance(
										map.getWidth()
												* (Main.Canvas.getIcon()
														.getIconWidth() / Main.w),
										map.getHeight()
												* (Main.Canvas.getIcon()
														.getIconHeight() / Main.h),
										Image.SCALE_DEFAULT);
						Main.Canvas.setIcon(new ImageIcon(scaled));
					}
				}
				map.setRGB(startNode.x, startNode.y, Color.blue.getRGB());
				Image scaled = map
						.getScaledInstance(
								map.getWidth()
										* (Main.Canvas.getIcon().getIconWidth() / Main.w),
								map.getHeight()
										* (Main.Canvas.getIcon()
												.getIconHeight() / Main.h),
								Image.SCALE_DEFAULT);
				Main.Canvas.setIcon(new ImageIcon(scaled));
			}
			endTime = System.currentTimeMillis();
			JOptionPane.showMessageDialog(Main.parentFrame,
					"Total elapsed time: " + (endTime - startTime) + " ms");
			return null;
		}
	}

	private class node {
		public int x, y, G, H, F;
		public node parent;
		public Boolean closed, open, isIllegal;

		public node(int X, int Y) {
			x = X;
			y = Y;
			G = -1;
			H = getH(x, y);
			F = H + G;
			parent = null;
			closed = false;
			open = false;
			isIllegal = false;
		}

		public node(int X, int Y, Boolean illegal) {
			x = X;
			y = Y;
			G = 0;
			H = getH(x, y);
			F = H + G;
			parent = null;
			closed = false;
			open = false;
			isIllegal = illegal;
		}

		int getH(int xPos, int yPos) {
			int manhattan = Math.abs(xPos - endCoords[0])
					+ Math.abs(yPos - endCoords[1]);
			return manhattan;
		}

	}
}
