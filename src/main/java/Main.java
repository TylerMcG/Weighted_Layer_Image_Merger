public class Main {
    /**
     * Main Method to Run
     * */
public static void main(String[] args)  {

    //populate the random collection with the weights for each file directory
    ImageGenerator.populateCollectionWeight(
            Constants.rcBody, Constants.body, Constants.START_WEIGHT_BODY, Constants.BODY_WEIGHT, Constants.bodyOdds );
    ImageGenerator.populateCollectionWeight(
            Constants.rcEyes, Constants.eyes, Constants.START_WEIGHT_EYES , Constants.EYES_WEIGHT, Constants.eyesOdds);
    ImageGenerator.populateCollectionWeight(
            Constants.rcMouth, Constants.mouth, Constants.START_WEIGHT_MOUTH, Constants.MOUTH_WEIGHT, Constants.mouthOdds);
    //Merges all images
    ImageGenerator.mergeImageLayers(Constants.body, Constants.eyes, Constants.mouth );

    ImageGenerator.ShowOdds();
    //Writes the image name, odds of the image, and the actual image itself to an excel file.
    WriteExcelFile.writeExcelFile(Constants.oddsMap);
}
}
