package ca.nrc.cadc.dlm;

import ca.nrc.cadc.util.StringUtil;
import javax.swing.plaf.synth.Region;
import org.apache.log4j.Logger;

public class DownloadTuple {
    private static Logger log = Logger.getLogger(DownloadTuple.class);

    //    tupleID (URI) will be translated into URI when needed
    //    this format required for use in DownloadIterators
    public final String tupleID;
    public final String shapeDescriptor;
    public final String label;
    // unsure we need Region or not - only interesting
    // in cadc-download-manager when DataLinkClient is putting
    // together cutouts
    private Region shape;

    public DownloadTuple(String tupleID, String shape, String label) {
        this.tupleID = tupleID;
        this.shapeDescriptor = shape;
        this.label = label;
    }

    /**
     * Convenience ctor to support historic use where only URI was provided.
     * @param tupleID String representing URI
     */
    public DownloadTuple(String tupleID) {
        this.tupleID = tupleID;
        this.shapeDescriptor = null;
        this.label = null;
    }

    public String toInternalFormat() {
        String tupleStr = tupleID;

        // This function might be able to provide different formats
        // within the shapeDescriptor to substitute whitespace for a different character
        if (StringUtil.hasLength(shapeDescriptor)) {
            tupleStr += "{" + shapeDescriptor + "}";
        }

        if (StringUtil.hasLength(label)) {
            tupleStr += "{" + label + "}";
        }

        return tupleStr;
    }
}
