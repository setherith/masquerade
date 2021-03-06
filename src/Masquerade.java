import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Masquerade {
	public JPanel output;
	public String[] pixels;
	private final int BLOCK_SIZE = 160;
	private final Dimension SIZE = new Dimension(BLOCK_SIZE, BLOCK_SIZE);
	private static String username;
	private Color background;
	private Color foreground;

	private boolean ConsolePrint, Graphics, Mono, Variant;
	
	public static void main(String[] args) {
		new Masquerade();
	}

	public Masquerade() {
		// defaults:
		ConsolePrint = false;
		Graphics = false;
		Mono = false;
		Variant = false;

		if (Graphics) {
			try {
				username = (String) JOptionPane.showInputDialog(null, "Username");
			} catch (NullPointerException e) {
				System.out.println("No username provided... quitting!");
			}
		} else {
			System.out.println("Enter username: ");
			username = new Scanner(System.in).nextLine();
		}

		pixels = new String[16];
		int pCount = 0;

		for (byte b : getCheckSum(username)) {
			if (b < 0)
				b *= -1;
			String fwd = String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0');
			String bwd = Reverse(fwd);
			String output = fwd + bwd;

			if (ConsolePrint)
				System.out.println(output);

			pixels[pCount] = output;
			pCount++;
		}

		background = RandomColour();
		foreground = RandomColour();

		if (Graphics) {
			JFrame app = new JFrame("Masquerade");

			output = new JPanel() {
				private static final long serialVersionUID = 1L;

				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					DrawMask(g);
				}
			};

			output.setPreferredSize(SIZE);

			app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			app.add(output);
			app.pack();

			app.setLocationRelativeTo(null);
			app.setVisible(Graphics);
		}

		System.out.println("Writting to file...");
		WriteFile();
		System.out.println("Finished!");
	}

	private void WriteFile() {
		BufferedImage bi = new BufferedImage(SIZE.width, SIZE.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();

		DrawMask(g);

		try {
			String fc = ConvertColourToString(foreground);
			String bc = ConvertColourToString(background);
			String filename = username + bc + fc + ".png";
			ImageIO.write(bi, "png", new File(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String ConvertColourToString(Color input) {
		return '#' + Integer.toHexString(input.getRGB()).substring(2);
	}

	private Graphics DrawMask(Graphics g) {
		g.setColor(background);
		g.fillRect(0, 0, SIZE.width, SIZE.height);

		int y = 0;

		g.setColor(foreground);
		for (String line : pixels) {
			int x = 0;
			for (char column : line.toCharArray()) {
				if (column == '1') {
					if (Variant) g.setColor(RandomColour());
					g.fillRect(x, y, 10, 10);
				}
				x += 10;
				if (x > SIZE.width)
					x = 0;
			}
			y += 10;
			if (y > SIZE.height)
				y = 0;
		}
		return g;
	}

	private Color RandomColour() {
		Random rand = new Random();
		if (Mono) {
			int c = rand.nextInt(255);
			return new Color(c, c, c);
		} else {
			return new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
		}
	}

	private String Reverse(String forward) {
		String result = "";
		char[] chars = forward.toCharArray();
		for (int i = chars.length - 1; i >= 0; i--) {
			result += chars[i];
		}
		return result;
	}

	private byte[] getCheckSum(String name) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(name.getBytes());
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}
}