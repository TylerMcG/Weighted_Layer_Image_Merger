# Weighted_Layer_Image_Merger
Author: Tyler McGregor
License: None

Mergers image layers based on a weighted value assigned to each layer determined by a formula.
Created as a proof of concept for party interested in generating NFTS by combining image assets 
with increasing rarity and being picked psuedo-randomly.

Currently this merges three layers together from three different directories: body, mouth, and eyes. Each directory has 
its own amount of files in it,so each directory is assigned its own randomCollection to add its own starting weight value A, 
and constant weight B to substract within the formula below.

__HOW TO ADD ADDITIONAL LAYERS AND CALCULATE ODDS__
In the constants folder create the following:
String addtionalPath = "path to addtional assets folder"
int startWeight = starting weight value
float weightConst = constant weight to substract from starting weight value
RandomCollection<Object> rcA = new RandomCollection<>();
String[] addtionalFiles = ImageGenerator.filesArray(addtionalPath)
int[] addtionalOdds = new int[addtionalFiles.length]

 In the ImageGenerator.mergeImageLayers() method you will need to add an additional argument for the addtionalFiles array.
 Within the method you will need to create an int index = getWeightedIndex(rcA, additionalFiles);
 Then create a new BufferedImage additionalIMG = ImageIO.read(new File(additionalFiles[index]));
 Use g.drawImage(additionalIMG, offsetX, offsetY, null); to add the layer to the combined image. Layers placed after
 this layer will be stacked on top so order is important to how the final image will be generated.
 
 Double imageOdds needs to add the following to update the odds calculation:
 (Math.pow(Constants.additionalOdds[index],2)/Constants.rcA.collectionSum())
 

__Explanation of the Weighted Value for each image asset in a directory__

      To make an asset image more rare, you can adjust its positioning in the directory, and/or by adjusting the A and B 
      weight values, or by adjusting the line
      
      total += Math.pow(weight, 2) in the Class RandomCollection .add(double weight, E result) method. 
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
     * multiplied by Σ(A-Bn)^2  (22855 in this example), where the closest value that is higher ( (A-Bn)^2 ) is chosen.
     * EXAMPLE:
     * RandomCollection.next() uses (randomDouble * total), to return the next highest entry in the map.
     * If randomDouble = 0.0565654 and total = 22855, then
     * (0.0565654 * 22855 = 1292.802217), map.higherEntry(1292.802217).getValue() = "image asset at index 3",
     * since the closest higher key value of "image asset at index 3" is 1849. (If wording is confusing see table above).
     * Below are two more examples of how changing the values of A and B affect the weight distribution for the i depth 
     * of the directory.
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
     
     *   if (A-Bn) < 0 then the value is automatically reverted to 1 in the ImageGenerator.populateMapWeight() method.
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
     * If one was interested in finding the probability of an combined image being generated, you just need to multiply 
     * the highest index weights together. In the above example, the chance of the rarest image being generated is
     * (25/22855) * (64/245) * (1/10002) = 2.85684E-8 or 0.000002856839372% chance of "randomly" being selected. 
     * This calculation could be useful for determining the worth of an image asset based on rarity properties. 
     * This has been added in the latest update and now writes this to an excel file along with the 
     * image name and the image itself. 
     ____________________________________________________________________________________________________________________________________________________________
     
 
