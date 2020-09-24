/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2011, 2020                       (c) 2011, 2020
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
*  $Revision: 5 $
*
************************************************************************
*/

package ca.nrc.cadc.dlm.handlers;

import ca.nrc.cadc.dlm.DownloadDescriptor;
import ca.nrc.cadc.dlm.DownloadGenerator;
import ca.nrc.cadc.dlm.DownloadTuple;
import ca.nrc.cadc.dlm.FailIterator;
import ca.nrc.cadc.dlm.SingleDownloadIterator;
import ca.nrc.cadc.util.CaseInsensitiveStringComparator;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/**
 * Download generator for CADC archive identifiers.
 *
 * @author pdowler
 */
public class AdDownloadGenerator implements DownloadGenerator {
    private static final Logger log = Logger.getLogger(AdDownloadGenerator.class);
    private String runID;
//    private static final Set<String> PARAMS;
//
//    static {
//        PARAMS = new TreeSet<String>(new CaseInsensitiveStringComparator());
//        PARAMS.add("logkey");
//        PARAMS.add("logvalue");
//        PARAMS.add("cutout");
//        PARAMS.add("runid");
//    }
    // TODO: cutout will have to be handled some other way

    private AdSchemeHandler ad;
    private Map<String, List<String>> params;

    public AdDownloadGenerator() {
        this.ad = new AdSchemeHandler();
    }

    public void setRunID(String runID) {
        this.runID = runID;
    }

    public Iterator<DownloadDescriptor> downloadIterator(DownloadTuple dt) {
        try {
            URL url = ad.getURL(dt.getID());
            StringBuilder sb = new StringBuilder(url.toExternalForm());
            sb.append("?runid=");
            sb.append(this.runID);
            // TODO: all of this is likely to be yanked, or have to be
            // dealt with at some time in future when the cutout & other parameters
            // are somehow represented here
//            if (params != null) {
//                StringBuilder sb = new StringBuilder(url.toExternalForm());
//                boolean first = true;
//                Iterator<Map.Entry<String, List<String>>> i = params.entrySet().iterator();
//                while (i.hasNext()) {
//                    Map.Entry<String, List<String>> me = i.next();
//                    if (PARAMS.contains(me.getKey()) && me.getValue() != null) {
//                        for (String v : me.getValue()) {
//                            if (first) {
//                                sb.append("?");
//                            } else {
//                                sb.append("&");
//                            }
//                            first = false;
//                            sb.append(me.getKey());
//                            sb.append("=");
//                            sb.append(URLEncoder.encode(v, "UTF-8"));
//                        }
//                    }
//                }
                url = new URL(sb.toString());
//            }
            return new SingleDownloadIterator(dt, url);
        } catch (Exception ex) {
            return new FailIterator(dt, "failed to resolve URI: " + ex.getMessage());
        }
    }

    public void setParameters(Map<String, List<String>> params) {
        this.params = params;
    }
}
