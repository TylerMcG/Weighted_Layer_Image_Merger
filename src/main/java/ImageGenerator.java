import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.*;

public class ImageGenerator {
    /**takes directory as String and creates an array with all files in the directory
     *
     * @param  dir - path to create array of all assets in directory
     * @return files[String]: returns String[] of filenames in directory path
     * @exception  NullPointerException : exception if directory path is incorrect
     */
    public static String[] fileArray(String dir) {
        String[] files = null;
        try {
            File f = new File(dir);
            files = f.list();
            for(int i = 0; i < Objects.requireNonNull(files).length; i++){
                files[i] = dir + "\\" + files[i];
            }

        } catch (NullPointerException e) {
            e.printStackTrace();

        }
        return files;

    }
    /**
     *
     * @param rc : Random Collection of Weighted Values for Assets in Directory
     * @param files : Array of Filenames in Directory
     * @param start : Start of Weighted Value
     * @param decreaseConst : Amount to Decrease at constant rate from the starting weighted Value
     * @param odds : Array used for adding probability for that image asset to be used later
     * @exception  NullPointerException : exception if directory path is incorrect
     * @exception  ArrayIndexOutOfBoundsException : Occurs if either files or odds array goes out of bounds
     *
     */
    public static void populateCollectionWeight(RandomCollection<Object> rc, String[] files, int start, float decreaseConst, int[] odds) {
        try {

            for (int i = 0; i < files.length; i++) {
                rc.add(start, files[i]); //add weight value to map
                odds[i] = start;
                start -= decreaseConst; //subtract weight constant
                if (start <= 0) { //gives a weight of 1 if file depth exceeds limit
                    start = 1;
                }
            }
        }
        catch (NullPointerException e) {
            System.err.println("Null  Pointer Exception. Directory path is incorrect!");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Array out of Bounds!");
        }
    }

    private static int getWeightedIndex(RandomCollection<Object> rc,
                                        String[] files){
        try {
            String fileToPull = String.valueOf(rc.next());
            if (Arrays.asList(files).contains(fileToPull)) {
                return Arrays.asList(files).indexOf(fileToPull);
            }

        }
        catch (NullPointerException e) {
            System.err.println("No files in Directory");
            e.printStackTrace();
        }
        //this should not occur but return first index as fail safe
        System.out.println("ERROR");
        return 0;
    }

    /**
     * @param body : array holding string value for all files in body folder
     * @param eyes :array holding string value for all files in eyes folder
     * @param mouth :array holding string value for all files in mouth folder
     * @exception IOException : Error while reading or writing files
     * @exception ArrayIndexOutOfBoundsException : Error if directory contains no images
     * @exception NullPointerException : Error if directory path is incorrect
     * No Output but this method is used to write the specified quantity of images to an output directory
     */
    public static void mergeImageLayers(String[] body, String[] eyes, String[] mouth) {
        for(int i = 0; i < Constants.NUM_IMG_TO_GEN; i++){
            try { //get index first so it can be used for determining probabilities later
                int bodyIndex = getWeightedIndex(Constants.rcBody, body);
                int mouthIndex = getWeightedIndex(Constants.rcMouth, mouth);
                int eyesIndex = getWeightedIndex(Constants.rcEyes, eyes);
                //get random body
                BufferedImage bodyIMG = ImageIO.read(new File(body[bodyIndex]));

                //get random eyes
                BufferedImage eyesIMG = ImageIO.read(new File(eyes[eyesIndex]));

                // get random mouth
                BufferedImage mouthIMG = ImageIO.read(new File(mouth[mouthIndex]));


                /* All images are same size 256x256 so picked an arbitrary img and divided by 5 to keep mouth and eyes
                from getting too far from  outside body. offset not necessary but a nice example of adding randomness
                to image generation. */
                int limitOffset = 5;
                // set randomX offSet
                int randomX = randomOffset(mouthIMG.getWidth()/limitOffset);
                // set randomY offSet
                int randomY = randomOffset(mouthIMG.getHeight()/limitOffset);

                //Create combined Image at exact same size
                int outImgWidth = bodyIMG.getWidth();
                int outImgHeight = bodyIMG.getHeight();

                BufferedImage combined = new BufferedImage(outImgWidth, outImgHeight, BufferedImage.TYPE_INT_ARGB);
                // paint images, preserving the alpha channels
                Graphics g = combined.getGraphics();
                g.drawImage(bodyIMG, 0, 0, null); //draws body first
                g.drawImage(eyesIMG, randomX, randomY, null); //draws eyes at random coordinates
                g.drawImage(mouthIMG, randomX, randomY, null); //draws mouth at random coordinates
                g.dispose(); //saves memory
                String filename = "img"; //creates base file name
                filename += i;  //adds value of iteration to make new file
                //output file
                File outputPath = new File(Constants.FILE_DIR_OUT_PATH + filename + ".png");
                Constants.outputFiles[i] = outputPath;
                ImageIO.write(combined, "PNG", outputPath); //writing file

                Double imageOdds = ((Math.pow(Constants.bodyOdds[bodyIndex],2)/ Constants.rcBody.collectionSum())
                        *(Math.pow(Constants.mouthOdds[mouthIndex],2)/ Constants.rcMouth.collectionSum())
                        * (Math.pow(Constants.eyesOdds[eyesIndex],2)/ Constants.rcEyes.collectionSum()) * 100);
                Constants.oddsMap.put(i, imageOdds);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Array index out of bounds");
                break;
            }
            catch (NullPointerException e) {
                System.err.println("Bad Directory Path");
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
                System.err.println("Bad Directory Path");
                break;
            }
        }
    }
    /**
    @param offset : amount to offset the image by.
    @return : returns a random integer in range of (0-offset) in positive or negative direction
     */
    private static int randomOffset(int offset){
        Random random = new Random();
        return new Random().nextInt(offset) * (random.nextBoolean() ? -1 : 1);
    }

    /**
     * Debugging tool used to make sure odds of excel file match actual odds of image
     */
    public static void ShowOdds() {
        Constants.oddsMap.forEach((key, value) ->
                System.out.println("Odds of " + key + " being generated is: " + value + "%"));
    }

}
