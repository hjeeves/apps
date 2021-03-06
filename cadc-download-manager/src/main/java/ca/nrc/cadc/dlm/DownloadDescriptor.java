/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2009.                            (c) 2009.
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

package ca.nrc.cadc.dlm;

import java.net.URL;
import java.util.Objects;

/**
 * Description of a download.
 *
 * @author pdowler
 */
public class DownloadDescriptor {
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

    public String status;
    public String uri;
    public URL url;
    public String destination;
    public String error;

    // TODO: Q: does 'uri' get replaced with 'DownloadTuple' across the
    // board here?

    /**
     * Constructor.
     *
     * @param url the URL to download the data from
     */
    public DownloadDescriptor(URL url) {
        this(null, url, null);
    }

    /**
     * Constructor.
     *
     * @param uri original URI (optional)
     * @param url the URL to download the data from
     */
    public DownloadDescriptor(String uri, URL url) {
        this(uri, url, null);
    }

    /**
     * Constructor. A download URL may be derived from a URI; the URI is
     * not used but just attached to aid in error reporting.
     *
     * @param uri         original URI (optional)
     * @param url         the URL to download the data from
     * @param destination the relative path where file should be stored (optional)
     */
    public DownloadDescriptor(String uri, URL url, String destination) {
        this.status = OK;
        this.uri = uri;
        this.url = url;
        this.destination = destination;
    }

    /**
     * Constructor for a download that could not be performed. The error message
     * describes the reason for failure.
     *
     * @param uri   original URI (optional)
     * @param error message describing the failure
     */
    public DownloadDescriptor(String uri, String error) {
        this(uri, error, null);
    }

    /**
     * Constructor for a download that could not be performed. The error message
     * describes the reason for failure.
     *
     * @param uri         (optional)
     * @param error       message describing the failure
     * @param destination the relative path where file would have been stored (optional)
     */
    public DownloadDescriptor(String uri, String error, String destination) {
        this.status = ERROR;
        this.uri = uri;
        this.error = error;
        this.destination = destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DownloadDescriptor that = (DownloadDescriptor) o;
        return Objects.equals(status, that.status)
            && Objects.equals(uri, that.uri)
            && Objects.equals(url, that.url)
            && Objects.equals(destination, that.destination)
            && Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {

        return Objects.hash(status, uri, url, destination, error);
    }

    @Override
    public String toString() {
        if (url != null) {
            return this.getClass().getSimpleName() + "["
                + status + "," + uri + "," + url + "," + destination + "]";
        }
        return this.getClass().getSimpleName() + "["
            + status + "," + uri + "," + error + "," + destination + "]";
    }

}
