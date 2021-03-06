/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c)2020.                             (c)2020.
*  Government of Canada                 Gouvernement du Canada
*  National Research Council            Conseil national de recherches
*  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
*  All rights reserved                  Tous droits réservés
*                                       
*  NRC disclaims any warranties,        Le CNRC dénie toute garantie
*  expressed, implied, or               énoncée, implicite ou légale,
*  statutory, of any kind with          de quelque nature que ce
*  respect to the software,             soit, concernant le logiciel,
*  including without limitation         y compris sans restriction
*  any warranty of merchantability      toute garantie de valeur
*  or fitness for a particular          marchande ou de pertinence
*  purpose. NRC shall not be            pour un usage particulier.
*  liable in any event for any          Le CNRC ne pourra en aucun cas
*  damages, whether direct or           être tenu responsable de tout
*  indirect, special or general,        dommage, direct ou indirect,
*  consequential or incidental,         particulier ou général,
*  arising from the use of the          accessoire ou fortuit, résultant
*  software.  Neither the name          de l'utilisation du logiciel. Ni
*  of the National Research             le nom du Conseil National de
*  Council of Canada nor the            Recherches du Canada ni les noms
*  names of its contributors may        de ses  participants ne peuvent
*  be used to endorse or promote        être utilisés pour approuver ou
*  products derived from this           promouvoir les produits dérivés
*  software without specific prior      de ce logiciel sans autorisation
*  written permission.                  préalable et particulière
*                                       par écrit.
*                                       
*  This file is part of the             Ce fichier fait partie du projet
*  OpenCADC project.                    OpenCADC.
*                                       
*  OpenCADC is free software:           OpenCADC est un logiciel libre ;
*  you can redistribute it and/or       vous pouvez le redistribuer ou le
*  modify it under the terms of         modifier suivant les termes de
*  the GNU Affero General Public        la “GNU Affero General Public
*  License as published by the          License” telle que publiée
*  Free Software Foundation,            par la Free Software Foundation
*  either version 3 of the              : soit la version 3 de cette
*  License, or (at your option)         licence, soit (à votre gré)
*  any later version.                   toute version ultérieure.
*                                       
*  OpenCADC is distributed in the       OpenCADC est distribué
*  hope that it will be useful,         dans l’espoir qu’il vous
*  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
*  without even the implied             GARANTIE : sans même la garantie
*  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
*  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
*  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
*  General Public License for           Générale Publique GNU Affero
*  more details.                        pour plus de détails.
*                                       
*  You should have received             Vous devriez avoir reçu une
*  a copy of the GNU Affero             copie de la Licence Générale
*  General Public License along         Publique GNU Affero avec
*  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
*  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
*                                       <http://www.gnu.org/licenses/>.
*
*  $Revision: 4 $
*
************************************************************************
*/

package ca.nrc.cadc.dlm.server;

import ca.nrc.cadc.dlm.DownloadRequest;
import ca.nrc.cadc.dlm.DownloadTuple;
import ca.nrc.cadc.dlm.DownloadTupleFormat;
import ca.nrc.cadc.dlm.DownloadTupleParsingException;
import ca.nrc.cadc.dlm.DownloadUtil;
import ca.nrc.cadc.net.ResourceNotFoundException;
import ca.nrc.cadc.rest.SyncInput;
import ca.nrc.cadc.util.StringUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * <p>Functions for parsing input parameters for Download Manager
 * </p>
 *
 * @author pdowler
 */
public class DLMInputHandler {
    // public API for DownloadManager is to accept and interpret these
    static final String PARAM_BAND_CUTOUT = "band";
    static final String PARAM_METHOD = "method";
    static final String PARAM_PARAMLIST = "params";
    static final String PARAM_PIXEL_CUTOUT = "cutout";
    static final String PARAM_POS_CUTOUT = "pos";
    static final String PARAM_TUPLE = "tuple";
    static final String PARAM_URI = "uri";

    static final List<String> INTERNAL_PARAMS = new ArrayList<String>();

    private static final Logger log = Logger.getLogger(DLMInputHandler.class);
    private static SyncInput si;
    private static HttpServletRequest servletRequest;
    private static DownloadTupleFormat downloadTupleFormat = new DownloadTupleFormat();

    static {
        INTERNAL_PARAMS.add(PARAM_URI);
        INTERNAL_PARAMS.add(PARAM_TUPLE);
        INTERNAL_PARAMS.add(PARAM_BAND_CUTOUT);
        INTERNAL_PARAMS.add(PARAM_PIXEL_CUTOUT);
        INTERNAL_PARAMS.add(PARAM_POS_CUTOUT);
        INTERNAL_PARAMS.add(PARAM_PARAMLIST);
        INTERNAL_PARAMS.add(PARAM_METHOD);
    }

    public DLMInputHandler(HttpServletRequest req) {
        this.servletRequest = req;
    }

    public void parseInput() throws DLMParsingException {
        try {
            si = new SyncInput(servletRequest, new DLMInlineContentHandler());
            si.init();
        } catch (IOException | ResourceNotFoundException ioe) {
            // TODO: make this more sanely split out & informative
            throw new DLMParsingException("couldn't parse request");
        }
    }

    /**
     * Extract all download content related parameters from the request.
     *
     * @return map of parameters included in request.
     */
    public static Map<String, List<String>> getParameters() {
        // internal repost
        String params = si.getParameter("params");

        if (params != null) {
            return DownloadUtil.decodeParamMap(params);
        }

        // original post
        Map<String, List<String>> paramMap = new TreeMap<>();
        Set<String> paramNames = si.getParameterNames();
        Iterator<String> paramNameIter = paramNames.iterator();

        // Build param map from full set provided
        while (paramNameIter.hasNext()) {
            String key = (String) paramNameIter.next();
            if (!INTERNAL_PARAMS.contains(key)) {
                List<String> values = si.getParameters(key);
                if (values != null && values.size() > 0) {
                    // Q: what does this need to look like when added to the param map?
                    // Q: what does it look like when it comes through?.. will need to hook debugger
                    // to it to see.
                    paramMap.put(key, values);
                }
            }
        }

        // same parsing needs to be done for fragment, etc. - but wait until after
        // it's handled for the original post.
        // not considered deprecated yet. parameters in 'fragment' will be
        // parsed into the paramMap
        List<String> frag = paramMap.get("fragment");
        if (frag != null) {
            for (String f : frag) {
                String[] parts = f.split("&");
                for (String p : parts) {
                    String[] kv = p.split("=");
                    if (kv.length == 2) {
                        List<String> values = paramMap.get(kv[0]);
                        if (values == null) {
                            values = new ArrayList<String>();
                            paramMap.put(kv[0], values);
                        }
                        values.add(kv[1]);
                    }
                }
            }
            paramMap.remove("fragment");
        }

        // Deprecated values need to be stripped out
        // Values for these will have been parsed out in handleDeprecatedAPI
        // Should only be 1 entry
        paramMap.remove("fileId");

        // can be more than one
        List<String> fcs = paramMap.get("fileClass");
        if (fcs != null) {
            for (String fc : fcs) {
                paramMap.remove(fc);
            }
            paramMap.remove("fileClass");
        }

        return paramMap;
    }

    /**
     * Extract all download content related parameters from the request.
     *
     * @return List of URIs.
     */
    public static DownloadRequest getDownloadRequest()  {

        // Parse input from the following sources:
        //  - PARAM_TUPLE (used as an internal forward parameter)
        //  - PARAM_URI (from external post)
        //  - PARAM_POS_CUTOUT
        //  - PARAM_BAND_CUTOUT
        //  - PARAM_PIXEL_CUTOUT
        //  - JSON payload information (from external post)
        // Merge resulting tuple lists from all sources

        // Inside si.init, form fields and json payload are processed. JSON payload
        // is put into into the content map using a key specified in the inlineContentHandler instance.
        // Form fields are put into a map called 'params' (just to make it more confusing compared to
        // what is already here...
        // in 'params' it's likely to find any of the deprecated data, so it will need to be
        // pruned in a manner similar to below, just removing the values from a different 'param'
        // instance.

        DownloadRequest dr = new DownloadRequest();

        // get runid, if present
        if (servletRequest.getParameter("runid") != null) {
            dr.runID = (String) servletRequest.getParameter("runid");
        }

        // This format is how the .jsp files pass tuple information in to DLM
        List<String> tupleStrList = si.getParameters(PARAM_TUPLE);
        if ((tupleStrList != null) && (tupleStrList.size() > 0)) {
            for (String u : tupleStrList) {
                if (StringUtil.hasText(u)) {
                    try {
                        dr.getTuples().add(downloadTupleFormat.parse(u));
                    } catch (DownloadTupleParsingException parseEx) {
                        dr.getValidationErrors().add(parseEx);
                    }
                }
            }
        }

        // Check to see if JSON content sent
        DownloadRequest downloadReq = (DownloadRequest)si.getContent(DLMInlineContentHandler.CONTENT_KEY);

        if (downloadReq != null) {
            // Merge any tuples & errors found into return list
            Set<DownloadTuple> jsonTuples = downloadReq.getTuples();
            if (jsonTuples != null && !jsonTuples.isEmpty()) {
                // merge the two lists
                for (DownloadTuple downloadTup : jsonTuples) {
                    dr.getTuples().add(downloadTup);
                }
            }

            List<Exception> validationErrs = downloadReq.getValidationErrors();
            if (validationErrs != null && !validationErrs.isEmpty()) {
                // merge the two lists
                for (Exception ex : validationErrs) {
                    dr.getValidationErrors().add(ex);
                }
            }
        }

        // Check to see if POS, BAND or CUTOUT parameters provided

        List<String> posCutoutParams = si.getParameters(PARAM_POS_CUTOUT);
        String posCutoutStr = null;
        if (posCutoutParams != null) {
            if (posCutoutParams.size() > 1) {
                throw new IllegalArgumentException("only one POS parameter allowed.");
            }
            posCutoutStr = posCutoutParams.get(0);
        }

        List<String> pixelCutoutParams = si.getParameters(PARAM_PIXEL_CUTOUT);
        String pixelCutout = null;
        if (pixelCutoutParams != null) {
            if (pixelCutoutParams.size() > 1) {
                throw new IllegalArgumentException("only one CUTOUT parameter allowed.");
            }
            pixelCutout = pixelCutoutParams.get(0);
        }

        List<String> bandCutoutParams = si.getParameters(PARAM_BAND_CUTOUT);
        String bandCutoutStr = null;
        if (bandCutoutParams != null) {
            if (bandCutoutParams.size() > 1) {
                throw new IllegalArgumentException("only one BAND parameter allowed.");
            }
            bandCutoutStr = bandCutoutParams.get(0);
        }

        // Check to see if any PARAM_URI entries are provided
        List<String> uriList = si.getParameters(PARAM_URI);

        // validate and add to list of tuples if possible.
        // otherwise add to ist of errors.
        if (uriList != null) {
            for (String u : uriList) {
                if (StringUtil.hasText(u)) {
                    try {
                        DownloadTuple dt = downloadTupleFormat.parse(u);
                        if (StringUtil.hasLength(posCutoutStr)) {
                            dt.posCutout = downloadTupleFormat.parsePosCutout(posCutoutStr);
                        }
                        if (StringUtil.hasLength(bandCutoutStr)) {
                            dt.bandCutout = downloadTupleFormat.parseBandCutout(bandCutoutStr);
                        }
                        if (StringUtil.hasLength(pixelCutout)) {
                            dt.label = downloadTupleFormat.convertLabelText(pixelCutout);
                        }
                        dr.getTuples().add(dt);
                    } catch (DownloadTupleParsingException parseEx) {
                        dr.getValidationErrors().add(parseEx);
                    }
                }
            }
        }

        // check for & attempt to process deprecated parameters
        handleDeprecatedAPI(dr);

        return dr;
    }

    /**
     * Check for deprecated parameters in the request. Convert to the most current for Download Manager's API.
     * Kept for backward compatibility.
     * @param dr DownloadRequest to put validated tuples or validation errors.
     *
     */
    public static void handleDeprecatedAPI(DownloadRequest dr) {

        String referer = si.getHeader("referer");
        List<String> sa;

        List<String> fileClasses = si.getParameters("fileClass");

        if (fileClasses != null) {
            // fileClass is a list of parameters giving other URIs
            log.debug("fileClass param(s): " + fileClasses.size());
            log.warn("deprecated param 'fileClass' used by " + referer);
            for (String fileClass : fileClasses) {
                log.debug("fileClass: " + fileClass);
                sa = si.getParameters(fileClass);
                if (sa != null) {
                    for (String curSa : sa) {
                        String u = processURI(curSa);
                        if (u != null) {
                            u = toAd(u);
                            log.debug("\turi: " + u);
                            try {
                                dr.getTuples().add(downloadTupleFormat.parse(u));
                            } catch (DownloadTupleParsingException parseEx) {
                                dr.getValidationErrors().add(parseEx);
                            }
                        }
                    }
                }
            }
        }

        sa = si.getParameters("fileId");
        if (sa != null) {
            log.debug("fileId param(s): " + sa.size());
            log.warn("deprecated param 'fileId' used by " + referer);
            for (String curSa : sa) {
                String u = processURI(curSa);
                if (u != null) {
                    u = toAd(u);
                    try {
                        dr.getTuples().add(downloadTupleFormat.parse(u));
                    } catch (DownloadTupleParsingException parseEx) {
                        dr.getValidationErrors().add(parseEx);
                    }
                }
            }
        }

    }


    /**
     * Validate content by trimming and checking length.
     *
     * @param uri - uri to be handled
     * @return valid (trimmed non-zero-length) string or null
     */
    private static String processURI(String uri) {
        String ret = null;
        if (uri != null) {
            ret = uri.trim();
            if (ret.length() == 0) {
                ret = null;
            }
        }
        return ret;
    }

    // CADC-specific backwards compat: prepend ad scheme if there is none
    private static String toAd(String s) {
        String[] parts = s.split(","); // comma-sep list
        if (parts.length == 1) {
            if (s.indexOf(':') > 0) { // already has a scheme
                return s;
            }
            log.debug("adding ad scheme to " + s);
            return "ad:" + s;
        }
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            s = toAd(part);
            if (s != null) {
                sb.append(s).append(",");
            }
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1); // trailing comma
        }
        return null;
    }

    /**
     * Parse tuples out of jdom2 XML document.
     * @param doc - Document containing tuple information
     * @return List of DownloadTuple
     */
    protected static DownloadRequest buildDownloadRequest(Document doc) {
        Element root = doc.getRootElement();
        Namespace ns = root.getNamespace();
        DownloadRequest downloadReq = new DownloadRequest();

        // Must be at least one
        if (root.getChildren("tuple", ns) != null) {
            // ... but may be more than one
            List<Element> tupleElements = root.getChildren("tuple", ns);

            for (Element tupleElement : tupleElements) {
                Element idEl = tupleElement.getChild("tupleID", root.getNamespace());
                String tupleID = idEl.getText();

                Element shapeEl = tupleElement.getChild("shape", root.getNamespace());
                String shape = shapeEl.getText();

                Element labelEl = tupleElement.getChild("label", root.getNamespace());
                String label = labelEl.getText();

                try {
                    DownloadTuple dt = downloadTupleFormat.parseUsingInternalFormat(tupleID, shape, null, label);
                    downloadReq.getTuples().add(dt);
                } catch (DownloadTupleParsingException parsingException) {
                    downloadReq.getValidationErrors().add(parsingException);
                }
            }
        }
        return downloadReq;
    }

}

