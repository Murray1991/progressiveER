package Experiments;


import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.datareader.entityreader.EntitySerializationReader;
import org.scify.jedai.datareader.groundtruthreader.GtSerializationReader;
import org.scify.jedai.utilities.datastructures.AbstractDuplicatePropagation;
import org.scify.jedai.utilities.datastructures.BilateralDuplicatePropagation;
import org.scify.jedai.utilities.datastructures.UnilateralDuplicatePropagation;

import java.util.List;

public class JedaiUtilities {

    //private final static String mainDirectoryDER = "/Users/leonardo/Desktop/data/dirtyErDatasets/";
    //private final static String mainDirectoryCER = "/Users/leonardo/Desktop/data/cleanCleanErDatasets/";
    private final static String mainDirectoryDER = "/Users/leonardo/Desktop/data/dirtyErDatasets/";
    private final static String mainDirectoryCER = "/Users/leonardo/Desktop/data/cleanCleanErDatasets/";

    //    private final static String mainDirectory = "/media/gap2/Data/Data/profiles/";

    private final static String[] entitiesPathDER = {
            "censusProfiles",
            "restaurantProfiles",
            "coraProfiles",
            "cddbProfiles"
    };

    private final static String[] entitiesPathCER = {
            "imdbProfiles",    // 0
            "dbpediaProfiles", // 1
    };


    private final static String mainGTDirectoryDER = "/Users/leonardo/Desktop/data/dirtyErDatasets/";
    private final static String mainGTDirectoryCER = "/Users/leonardo/Desktop/data/cleanCleanErDatasets/";

    private final static String[] entitiesPathDER_GT = {
            "censusIdDuplicates",
            "restaurantIdDuplicates",
            "coraIdDuplicates",
            "cddbIdDuplicates"
    };

    private final static String[] entitiesPathCER_GT = {
            "moviesIdDuplicates",
            "restaurantIdDuplicates",
            "coraIdDuplicates",
            "cddbIdDuplicates"
    };

    public static List<EntityProfile> getEntities(int datasetId, boolean clean) {
        String eFile;
        if (clean)
            eFile = mainDirectoryCER + entitiesPathCER[datasetId];
        else
            eFile = mainDirectoryDER + entitiesPathDER[datasetId];

        EntitySerializationReader eReader = new EntitySerializationReader(eFile);
        return eReader.getEntityProfiles();
    }

    public static AbstractDuplicatePropagation getGroundTruth(int datasetId, boolean clean) {
        String gtFile = clean ? mainGTDirectoryCER + entitiesPathCER[datasetId] :
                mainGTDirectoryDER + entitiesPathDER_GT[datasetId];
        GtSerializationReader gtReader = new GtSerializationReader(gtFile);

        if (clean)
            return new BilateralDuplicatePropagation(gtReader.getDuplicatePairs(null));
        else
            return new UnilateralDuplicatePropagation(gtReader.getDuplicatePairs(null));
    }
}
