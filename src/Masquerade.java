/*Manifesto:
 * To take a username and convert it to a bitmap image for a profile
 * on any future sites for when a user doesn't add their own picture. */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Masquerade {
	public JPanel output;
	public static String[] pixels;

	public static void main(String[] args) {
		
		pixels = new String[16];
		int pCount = 0;
		
		for (byte b : getCheckSum("abi")) {
			if (b < 0)
				b *= -1;
			String fwd = String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0');
			String bwd = Reverse(fwd);
			String output = fwd + bwd;
			System.out.println(output);
			
			pixels[pCount] = output;
			pCount++;
		}
		
		new Masquerade();
	}

	private static Color RandomColour() {
		Random rand = new Random();
		int r = rand.nextInt(255);
		return new Color(r, r, r);
	}
	
	private static String Reverse(String forward) {
		String result = "";
		char[] chars = forward.toCharArray();
		for (int i = chars.length - 1; i >= 0; i--) {
			result += chars[i];
		}
		return result;
	}

	public Masquerade() {
		JFrame app = new JFrame("NameGraphic");
		
		output = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				g.setColor(RandomColour());
				g.fillRect(0, 0, 160, 160);
				
				int y = 0;
				
				g.setColor(RandomColour());
				for (String line : pixels) {
					int x = 0;
					for (char column : line.toCharArray()) {
						if (column == '1') 
							g.fillRect(x, y, 10, 10);
						x += 10;
						if (x > 160) x = 0;
					}
					y += 10;
					if (y > 160) y = 0;
				}
				
			}
		};
		
		output.setPreferredSize(new Dimension(160, 160));
		
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.add(output);
		app.pack();
		
		app.setLocationRelativeTo(null);
		app.setVisible(true);
	}

	public static byte[] getCheckSum(String name) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(name.getBytes());
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}
}