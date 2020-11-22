package DataStructures;

import com.esotericsoftware.minlog.Log;
import org.apache.jena.atlas.json.JsonArray;
import org.scify.jedai.datareader.AbstractReader;
import DataStructures.EntityProfile;

import java.util.ArrayList;
import java.util.List;

public class NewEntitySerializationReader extends AbstractReader {

    protected final List<DataStructures.EntityProfile> entityProfiles = new ArrayList();

    public NewEntitySerializationReader(String filePath) {
        super(filePath);
    }

    public List<EntityProfile> getEntityProfiles() {
        Attribute attribute;
        if (!this.entityProfiles.isEmpty()) {
            return this.entityProfiles;
        } else if (this.inputFilePath == null) {
            Log.error("Input file path has not been set!");
            return null;
        } else {
            List<org.scify.jedai.datamodel.EntityProfile> objects = (List) loadSerializedObject(this.inputFilePath);
            for (org.scify.jedai.datamodel.EntityProfile e : objects) {
                this.entityProfiles.add(new EntityProfile(e));
            }
            return this.entityProfiles;
        }
    }

    @Override
    public String getMethodConfiguration() {
        return null;
    }

    @Override
    public String getMethodInfo() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public String getMethodParameters() {
        return null;
    }

    @Override
    public JsonArray getParameterConfiguration() {
        return null;
    }

    @Override
    public String getParameterDescription(int i) {
        return null;
    }

    @Override
    public String getParameterName(int i) {
        return null;
    }
}
