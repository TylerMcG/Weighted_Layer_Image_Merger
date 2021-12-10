import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class ImageGenerator {
    private static final String FILE_DIR_OUT_PATH = "Output Path";

    private static final int NUM_IMG_TO_GEN = 100;

    private static final int START_WEIGHT_BODY = 20;
    private static final int BODY_WEIGHT = 5;

    private static final int START_WEIGHT_EYES = 100;
    private static final int EYES_WEIGHT = 19;

    private static final int START_WEIGHT_MOUTH = 10;
    private static final int MOUTH_WEIGHT = 3;

    private static final RandomCollection<Object> rcBody = new RandomCollection<>();
    private static final RandomCollection<Object> rcEyes = new RandomCollection<>();
    private static final RandomCollection<Object> rcMouth = new RandomCollection<>();

    /**takes directory as String and creates an array with all files in the directory
     *
     * @param  dir - path to create array of all assets in directory
     * @return files[String]: returns String[] of filenames in directory path
     * @exception  NullPointerException : exception if directory path is incorrect
     */
    private static String[] fileArray(String dir) {
        String[] files = null;
        try {
            File f = new File(dir);
            files = f.list();
            for(int i = 0; i < Objects.requireNonNull(files).length; i++){
                files[i] = dir + "\\" + files[i];
            }


        } catch (NullPointerException e) {
            System.out.println("Directory path is Incorrect");

        }
        return files;

    }
    /**
     *
     * @param rc : Random Collection of Weighted Values for Assets in Directory
     * @param files : Array of Filenames in Directory
     * @param start : Start of Weighted Value
     * @param decreaseConst : Amount to Decrease at constant rate from the starting weighted Value
     * @exception  NullPointerException : exception if directory path is incorrect
     *
     * Formula for determining the percentage an asset is likely to be chosen for a layer in the image merger:
     *     (A-Bn) / Σ((A-Bn)^2) n = 0:i , where
     *     A = starting weight value
     *     B = constant to subtract from starting weight
     *     n = index in summation
     *     i = total indexes in file path : example below has 6 files (0-5)
     *     0:i = each step in summation from 0 to i
     *     Σ(A-Bn) = summation at each step from 0:i
     *     ^2 = squared
     *    EXAMPLE VALUES AND TABLE:
     *     A = 100
     *     B = 19
     *     i = 5
     *
     *     ----------------------------------------------------------------------------------
     *     |   N Index  |    0    |    1    |     2    |     3     |     4     |     5    |
     *     ----------------------------------------------------------------------------------
     *     |   (A-Bn)   |   100   |    81   |    62    |    43     |    24     |     5    |
     *     ---------------------------------------------------------------------------------
     *     | (A-Bn)^2   |  10000  |   6561  |   3844   |   1849    |    576    |    25    |
     *     --------------------------------------------------------------------------------
     *     | Σ(A-Bn)^2  |  22855  |  22855  |   22855  |   22855   |   22855   |   22855  |
     *     --------------------------------------------------------------------------------
     *     |  Weight %  | 43.75%  | 28.71%  |  16.82%  |   8.090%  |   2.520%  |  0.109%  |
     *     ---------------------------------------------------------------------------------
     *
     * The weight that is actually used to determine how an image asset is pulled is a random double (0.0 - 1.0)
     * multiplied by Σ(A-Bn)^2  (22855 in this example), where the closet value that is higher ( (A-Bn)^2 ) is chosen.
     *  So if the random double = 0.0565654, then the image asset that would be chosen in this example would be
     *  (0.0565654 * 22855 = 1292.802217) where map.higherEntry(1292.802217).getValue() = "filename at index 3", since
     *  the next highest entry in the map is (1849, "filename at index 3").
     *  Below are two more examples of how changing the values of A and B affect the weight distribution.
     *
     *  A = 10
     *  B = 1
     *  i = 2
     *     ---------------------------------------------
     *     |   N Index  |    0     |    1    |     2
     *     ---------------------------------------------
     *     |   (A-Bn)   |   10    |    9    |      8
     *     ---------------------------------------------
     *     | (A-Bn)^2   |  100    |   81    |     64
     *     ---------------------------------------------
     *     | Σ(A-Bn)^2  |  245    |   245   |    245
     *     ---------------------------------------------
     *     |  Weight %  | 40.82%  | 33.06%  |  26.12%
     *     ---------------------------------------------
     *   A = 100
     *   B = 1000
     *   i = 2
     *   if (A-Bn) < 0 then the value is automatically reverted to 1
     *     ---------------------------------------------
     *    |   N Index  |    0     |     1     |     2
     *     ---------------------------------------------
     *    |   (A-Bn)   |   100    |     1     |     1
     *     ---------------------------------------------
     *    | (A-Bn)^2   |  10000   |     1     |     1
     *    ---------------------------------------------
     *    | Σ(A-Bn)^2  |  10002   |   10002   |  10002
     *    ---------------------------------------------
     *    |  Weight %  |  99.98%  | .000099%  |.000099%
     *    ---------------------------------------------
     *
     */
    private static void populateCollectionWeight(RandomCollection<Object> rc, String[] files, int start, int decreaseConst) {
        try {
            for (String file : files) {
                rc.add(start, file); //add weight value to map
                start -= decreaseConst; //subtract weight constant
                if (start <= 0) { //gives a weight of 1 if file depth exceeds limit with (A-Bn)
                    start = 1;
                }
            }
        }
        catch (NullPointerException e) {
            System.err.println("Null  Pointer Exception. Directory path is incorrect!");
        }
    }

    private static int getWeightedIndex(RandomCollection<Object> rc,
                                        String[] files){
        try {
            String fileToPull = String.valueOf(rc.next());
            if (Arrays.asList(files).contains(fileToPull)) {
                return Arrays.asList(files).indexOf(fileToPull);
            }
            else { // Error handling returns first index
                System.out.println("CAUGHT");
                return 0;
            }
        }
        catch (NullPointerException e) {
            System.err.println("No files in Directory");
            e.printStackTrace();
        }
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
    private static void combineImages (String[] body, String[] eyes, String[] mouth) {
        for(int i = 0; i < ImageGenerator.NUM_IMG_TO_GEN; i++){
            try {
                //get weighted body
                BufferedImage bodyIMG = ImageIO.read(new File(body[getWeightedIndex(rcBody, body)]));

                //get weighted eyes
                BufferedImage eyesIMG = ImageIO.read(new File(eyes[getWeightedIndex(rcEyes, eyes)]));

                // get weighted mouth
                BufferedImage mouthIMG = ImageIO.read(new File(mouth[getWeightedIndex(rcMouth, mouth)]));


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

                BufferedImage combined =
                        new BufferedImage(outImgWidth, outImgHeight, BufferedImage.TYPE_INT_ARGB);
                // paint images, preserving the alpha channels
                Graphics g = combined.getGraphics();
                g.drawImage(bodyIMG, 0, 0, null); //draws body first
                g.drawImage(eyesIMG, randomX, randomY, null); //draws eyes at random coordinates
                g.drawImage(mouthIMG, randomX, randomY, null); //draws mouth at random coordinates

                g.dispose(); //saves memory
                String filename = "img"; //creates base file name
                filename += String.valueOf(i); //adds value of iteration to make new file
                File outputPath = //output file path and image
                        new File(FILE_DIR_OUT_PATH + filename + ".png");
                ImageIO.write(combined, "PNG", outputPath); //writing file
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
    * Main Method to Run
    * */
    public static void main(String[] args)  {
        String dirBody = "Path to body folder";
        String dirEyes = "Path to eyes folder";
        String dirMouth = "Path to mouth folder";
        String[] body = fileArray(dirBody);
        String[] eyes = fileArray(dirEyes);
        String[] mouth = fileArray(dirMouth);
        //populate the random collection with the weights for each file directory
        populateCollectionWeight(rcBody, body, START_WEIGHT_BODY, BODY_WEIGHT);
        populateCollectionWeight(rcEyes, eyes, START_WEIGHT_EYES ,EYES_WEIGHT);
        populateCollectionWeight(rcMouth, mouth, START_WEIGHT_MOUTH, MOUTH_WEIGHT);
        combineImages(body, eyes, mouth);
    }
}
