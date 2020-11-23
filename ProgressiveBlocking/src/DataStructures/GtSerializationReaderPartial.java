package DataStructures;

import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.datamodel.IdDuplicates;
import org.scify.jedai.datareader.AbstractReader;
import org.scify.jedai.datareader.entityreader.AbstractEntityReader;
import org.scify.jedai.datareader.groundtruthreader.GtSerializationReader;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GtSerializationReaderPartial extends GtSerializationReader {

    private int limitD1;
    private int limitD2;

    public GtSerializationReaderPartial(String filePath, int limitD1, int limitD2) {
        super(filePath);
        this.limitD1 = limitD1;
        this.limitD2 = limitD2;
    }

    @Override
    public Set<org.scify.jedai.datamodel.IdDuplicates> getDuplicatePairs(List<EntityProfile> profilesD1, List<EntityProfile> profilesD2) {
        if (!idDuplicates.isEmpty()) return idDuplicates;

        idDuplicates.addAll(
                ( (Set<IdDuplicates>) loadSerializedObject(inputFilePath)).stream().filter(
                        d -> d.getEntityId1() <= limitD1 && d.getEntityId2() <= limitD2
                ).collect(Collectors.toSet())
        );
        return idDuplicates;
    }
}
