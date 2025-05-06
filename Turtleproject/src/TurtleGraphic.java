import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import uk.ac.leedsbeckett.oop.LBUGraphics;

public class TurtleGraphic extends LBUGraphics {
    private List<String> commandHistory = new ArrayList<>();
    private boolean unsavedImage = false;
    private boolean unsavedCommands = false;
    private boolean savedCommands = false;
    private boolean isPenDown = true;
    private int penWidth = 1;

    public static void main(String[] args) {
        new TurtleGraphic();  
    }

    public TurtleGraphic() {
        JFrame MainFrame = new JFrame();
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.setLayout(new FlowLayout());
        MainFrame.add(this);
        MainFrame.pack();
        MainFrame.setVisible(true);
        setPenColour(java.awt.Color.BLUE);
    }

    public void processCommand(String command) {
        commandHistory.add(command);
        unsavedCommands = true;
        command = command.toLowerCase();
        String[] temp = command.split(" ");
        String cmd = temp[0];

        System.out.println("Command typed was: " + cmd);

        switch (cmd) {
            case "right":
                if (temp.length > 1) {
                    try {
                        int angle = Integer.parseInt(temp[1]);
                        if (angle < 0) {
                            System.out.println(" Angle must be greater than zero.");
                            break;
                        }
                        right(angle);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid parameter: '" + temp[1] + "'. Please enter a number.");
                    }
                } else {
                    System.out.println("Please enter a parameter after the command");
                }
                break;

            case "left":
                if (temp.length > 1) {
                    try {
                        int angle = Integer.parseInt(temp[1]);
                        if (angle < 0) {
                            System.out.println(" Angle must be greater than zero.");
                            break;
                        }
                        left(angle);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid parameter: '" + temp[1] + "'. Please enter a number.");
                    }
                } else {
                    System.out.println("Please enter a parameter after the command");
                }
                break;

            case "pendown":
                isPenDown = true;
                drawOn();
                System.out.println("Pen is DOWN (drawing enabled)");
                break;
            case "penup":
                isPenDown = false;
                drawOff();
                System.out.println("Pen is UP (drawing disabled)");
                break;

            case "move":
                drawOn();
                if (temp.length > 1) {
                    try {
                        int dist = Integer.parseInt(temp[1]);
                        if (dist <= 0 ) {
                            System.out.println(" Distance must be greater than zero");
                            break;
                        }
                        forward(dist);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid parameter: '" + temp[1] + "'. Please enter a number.");
                    }
                } else {
                    System.out.println("Please enter a parameter after the command");
                }
                break;


            case "reverse":
                if (temp.length > 1) {
                    try {
                        int dist = Integer.parseInt(temp[1]);
                        if (dist < 0) {
                            System.out.println("Distance must be greater than zero.");
                            break;
                        }
                        right(180);
                        forward(dist);  
                        right(180);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid parameter: '" + temp[1] + "'. Please enter a number.");
                    }
                } else {
                    System.out.println("Please enter a parameter after the command");
                }
                break;

            case "reset":
                reset();
                break;

            case "clear":
                clear();
                break;

            case "color":
                if (temp.length > 1) {
                    switch (temp[1]) {
                        case "red":
                            setPenColour(java.awt.Color.RED);
                            break;
                        case "green":
                            setPenColour(java.awt.Color.GREEN);
                            break;
                        case "blue":
                            setPenColour(java.awt.Color.BLUE);
                            break;
                        case "white":
                            setPenColour(java.awt.Color.WHITE);
                            break;
                        case "black":
                            setPenColour(java.awt.Color.BLACK);
                            break;
                        default:
                            System.out.println(" Unknown color: " + temp[1]);
                    }
                } else {
                    System.out.println(" Please specify a color.");
                }
                break;

            case "about":
                about();
                break;

            case "square":
                if (temp.length > 1) {
                    try {
                        int length = Integer.parseInt(temp[1]);
                        if (length < 0 ) {
                            System.out.println(" Length must be greater than zero.");
                            break;
                        }
                        drawSquare(length);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid parameter: '" + temp[1] + "'. Please enter a number.");
                    }
                } else {
                    System.out.println(" Please provide a length for the square.");
                }
                break;

            case "pen":
                if (temp.length == 4) {
                    try {
                        int r = Integer.parseInt(temp[1]);
                        int g = Integer.parseInt(temp[2]);
                        int b = Integer.parseInt(temp[3]);
                        if ((r < 0 || r > 255) || (g < 0 || g > 255) || (b < 0 || b > 255)) {
                            System.out.println("Error: RGB values must be between 0 and 255");
                            break;
                        }
                        setPenColour(new java.awt.Color(r, g, b));
                        System.out.println("Pen color set to RGB(" + r + ", " + g + ", " + b + ")");
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid RGB values. Use format: pen 255 0 0");
                    }
                } else {
                    System.out.println("Error: Please provide exactly three RGB values (0-255). Example: pen 255 0 0");
                }
                break;

            case "penwidth":
                if (temp.length == 2) {
                    try {
                        int width = Integer.parseInt(temp[1]);
                        if (width <= 0) {
                            System.out.println("Error: Pen width must be greater than 0");
                            break;
                        }
                        if (width > 50) {
                            System.out.println("Warning: Very large pen width (" + width + ") may affect performance");
                        }
                        setStroke(width);
                        System.out.println("Pen width set to " + width);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid pen width. Please enter a positive integer");
                    }
                } else {
                    System.out.println("Error: Please provide exactly one width value. Example: penwidth 5");
                }
                break;

            case "triangle":
                if (temp.length > 1) {
                    try {
                        int length = Integer.parseInt(temp[1]);
                        if (length < 0 ) {
                            System.out.println(" Length must be greater than zero");
                            break;
                        }
                        drawTriangle(length);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid parameter: '" + temp[1] + "'. Please enter a number.");
                    }
                } else {
                    System.out.println(" Please provide a side length for the triangle.");
                }
                break;

            case "triangle3":
                if (temp.length == 4) {
                    try {
                        int side1 = Integer.parseInt(temp[1]);
                        int side2 = Integer.parseInt(temp[2]);
                        int side3 = Integer.parseInt(temp[3]);

                        if (side1 <= 0 || side2 <= 0 || side3 <= 0 || side1 > 1000 || side2 > 1000 || side3 > 1000) {
                            System.out.println("Side lengths must be between 1 and 1000.");
                            break;
                        }

                        if (side1 + side2 <= side3 || side1 + side3 <= side2 || side2 + side3 <= side1) {
                            System.out.println("These sides do not form a valid triangle.");
                            break;
                        }

                        drawCustomTriangle(side1, side2, side3);

                    } catch (NumberFormatException e) {
                        System.out.println("Please enter valid numbers. Example: triangle3 100 80 60");
                    }
                } else {
                    System.out.println("Provide exactly three sides. Example: triangle3 100 80 60");
                }
                break;




            case "star":
                if (temp.length > 1) {
                    try {
                        int size = Integer.parseInt(temp[1]);
                        if (size <= 0) {
                            System.out.println("Error: Star size must be greater than zero");
                            break;
                        }
                        drawStar(size);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number for star size");
                    }
                } else {
                    System.out.println("Please enter a size parameter after the command");
                }
                break;

            case "saveimage":
                saveImageToFile();
                break;

            case "savecommands":
                saveCommandsToFile();
                break;

            case "loadcommands":
                if (unsavedCommands || unsavedImage) {
                    int confirm = JOptionPane.showConfirmDialog(null, 
                        "You have unsaved changes. Do you want to continue and lose unsaved work?", 
                        "Unsaved Changes", JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) {
                        break;
                    }
                }
                loadCommandsFromFile();
                break;

            case "exit":
                System.out.println("Exiting...");
                System.exit(0);
                break;

            default:
                System.out.println(" Unknown command: " + command);
        }
    }

    @Override
    public void about() {
        super.about(); 
        System.out.println("Nehal "); 
    }
    @Override
    public void forward(int distance) {
        if (isPenDown) {
            drawOn();
        } else {
            drawOff();
        }
        super.forward(distance);
    }

    public void drawSquare(int length) {
        drawOn();  
        if (getPenColour() == null) {
            setPenColour(java.awt.Color.BLACK); 
        }
        for (int i = 0; i < 4; i++) {
            forward(length);
            right(90);
        }
    }

    public void reverse(int distance) {
        if (distance < 0 ) {
            System.out.println(" Distance must be greater than zero");
            return;
        }
        drawOff();
        right(180);
        forward(distance);
        right(180);
        drawOn();
    }

    public void drawTriangle(int length) {
        drawOn();
        if (getPenColour() == null) {
            setPenColour(java.awt.Color.BLACK);
        }
        for (int i = 0; i < 3; i++) {
            forward(length);
            right(120);
        }
    }

    public void drawCustomTriangle(int a, int b, int c) {
        double angleA = Math.toDegrees(Math.acos((b*b + c*c - a*a) / (2.0 * b * c)));
        double angleB = Math.toDegrees(Math.acos((a*a + c*c - b*b) / (2.0 * a * c)));
        double angleC = 180.0 - angleA - angleB;

        drawOn();

       
        forward(a);

        right(180 - (int)Math.round(angleC));
        forward(b);

        right(180 - (int)Math.round(angleA));
        forward(c);

            }
   


    public void drawStar(int size) {
        Color currentColor = getPenColour();
        setPenColour(Color.YELLOW);
        drawOn();
        for (int i = 0; i < 5; i++) {
            forward(size);
            right(144);
        }
        setPenColour(currentColor);
    }
    

    public void setStroke(int width) {
        Graphics2D g2d = (Graphics2D) getGraphics();
        if (g2d != null) {
            g2d.setStroke(new java.awt.BasicStroke(width));
        }
    }

    public void saveImageToFile() {
        try {
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
            int option = chooser.showSaveDialog(null);
            if (option == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File file = chooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new java.io.File(file.getAbsolutePath() + ".png");
                }
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                    getWidth(), getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g = img.createGraphics();
                paint(g);
                g.dispose();
                javax.imageio.ImageIO.write(img, "png", file);
                System.out.println("Image saved: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Failed to save image.");
        }
    }

    public void saveCommandsToFile() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String cmd : commandHistory) {
                    writer.println(cmd);
                }
                savedCommands = true;
                unsavedCommands = false;
                System.out.println("Commands saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Failed to save commands: " + e.getMessage());
            }
        }
    }

    public void loadCommandsFromFile() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                clear(); 
                reset();
                while ((line = reader.readLine()) != null) {
                    processCommand(line);
                }
                unsavedCommands = false;
                System.out.println("Commands loaded from: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Failed to load commands: " + e.getMessage());
            }
        }
    }
}
