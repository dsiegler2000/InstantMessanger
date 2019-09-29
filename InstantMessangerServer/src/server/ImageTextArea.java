package server;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.util.ArrayList;

import javax.swing.JTextArea;

public class ImageTextArea extends JTextArea {
    
    private final ArrayList<BufferedImage> images = new ArrayList<>();
    private final ArrayList<Integer> xPoses = new ArrayList<>();
    private final ArrayList<Integer> yPoses = new ArrayList<>();
    private final ArrayList<Integer> xSizes = new ArrayList<>();
    private final ArrayList<Integer> ySizes = new ArrayList<>();
    
    public void addImage(BufferedImage image, int xPos, int yPos, int xSize, int ySize){
        
        this.images.add(image);
        this.xPoses.add(xPos);
        this.yPoses.add(yPos);
        this.xSizes.add(xSize);
        this.ySizes.add(ySize);
        
        invalidate();
        repaint();
        
    }
    
    @Override
    public void paintComponent(Graphics g){
        
        super.paintComponent(g);
        
        for(int i = 0; i < images.size(); i++){
            
            if(images.get(i) != null){
                
                g.drawImage(images.get(i), xPoses.get(i), yPoses.get(i),
                        xPoses.get(i) + xSizes.get(i), yPoses.get(i) + ySizes.get(i), 
                        0, 0, images.get(i).getWidth(), images.get(i).getHeight(), this);
                
            }
            
        }
            
    }
    
}
