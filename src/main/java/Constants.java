import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class Constants {
    //All constants to be used throughout the app

    //total images to generate
    static final int NUM_IMG_TO_GEN = 100;
    //File_DIR_OUT_PATH is the the output path to save all images
    static final String FILE_DIR_OUT_PATH = "File path to save images to";
    //directory location as string for each asset folder
    static final String dirBody = "filepath for body assets folder";
    static final String dirEyes = "filepath for eyes assets folder";
    static final String dirMouth = "filepath for mouth assets folder";

    //constants for starting weight and subtracting constant weight in probability formula,
    // odds array is used for calculating the chance of that asset being picked, collection populates weights
    static final int START_WEIGHT_EYES = 100;
    static final float EYES_WEIGHT = 19;
    static final String[] eyes = ImageGenerator.fileArray(Constants.dirEyes);
    static final int[] eyesOdds = new int[eyes.length];
    static final RandomCollection<Object> rcEyes = new RandomCollection<>();

    static final int START_WEIGHT_BODY = 20;
    static final float BODY_WEIGHT = 5;
    static final String[] body = ImageGenerator.fileArray(Constants.dirBody);
    static int[] bodyOdds = new int[body.length];
    static final RandomCollection<Object> rcBody = new RandomCollection<>();

    static final int START_WEIGHT_MOUTH = 10;
    static final float MOUTH_WEIGHT = 3;
    static final String[] mouth = ImageGenerator.fileArray(Constants.dirMouth);
    static int[] mouthOdds = new int[mouth.length];
    static final RandomCollection<Object> rcMouth = new RandomCollection<>();

    //limits offset of random amount to move assets by
    static final int limitOffsetX = 5;
    static final int limitOffsetY = 5;

    static String[] columns = {"Image Name", "Rarity as %", "Image"};
    //map used to store <filename, totalOdds> for that image asset being created an outputted in ImageGenerator.showOdds();
    static final Map<Integer, Double> oddsMap = new TreeMap<Integer, Double>() {};
    static final File[] outputFiles = new File[NUM_IMG_TO_GEN];

}
