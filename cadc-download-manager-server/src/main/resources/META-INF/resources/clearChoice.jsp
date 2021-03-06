<!--
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2020.                            (c) 2020.
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
-->


<%@ taglib uri="WEB-INF/c.tld" prefix="c"%>
<%@ page import="ca.nrc.cadc.dlm.server.SkinUtil" %>
<%@ page import="ca.nrc.cadc.dlm.DownloadRequest" %>
<%@ page import="ca.nrc.cadc.dlm.server.DispatcherServlet" %>
<%@ page import="ca.nrc.cadc.dlm.DownloadTuple" %>
<%@ page import="java.util.Set" %>
<%@ page import="ca.nrc.cadc.util.StringUtil" %>
<jsp:useBean id="dtFormat" class="ca.nrc.cadc.dlm.DownloadTupleFormat"/>

<%
    DownloadRequest downloadReq = (DownloadRequest)request.getAttribute(DispatcherServlet.INTERNAL_FORWARD_PARAMETER);
    Set<DownloadTuple> tupleList = downloadReq.getTuples();

    // If calling program has provided values they should be here
    String headerURL = SkinUtil.headerURL;
    String footerURL = SkinUtil.footerURL;
    String bodyHeaderURL = "";
    String skinURL = SkinUtil.skinURL;

    if (!StringUtil.hasLength(headerURL)) {
        if (!StringUtil.hasLength(skinURL)) {
            skinURL = "http://localhost/cadc/skin/";
        }

        if (!skinURL.endsWith("/")) {
            skinURL += "/";
        }

        if (!(skinURL.startsWith("http://") || skinURL.startsWith("https://"))) {
            if (!skinURL.startsWith("/")) {
                skinURL = "/" + skinURL;
            }
            skinURL = "http://localhost" + skinURL;
        }

        headerURL = skinURL + "htmlHead";
        bodyHeaderURL = skinURL + "bodyHeader";
        footerURL = skinURL + "bodyFooter";
    }
%>

<html>
<head>
    <c:catch><c:import url="<%= headerURL %>" /></c:catch>
<script type="text/javascript">
	function closemyself()
	{
		window.opener = self;
		self.close();
	}
</script>
</head>

<body onLoad="setTimeout('closemyself()', 10000);self.focus()" >
<c:catch><c:import url="<%= bodyHeaderURL %>" /></c:catch>

<br>
<br>
 <div style="padding-left: 2em; padding-right: 2em">
        <table width="95%">
            <tbody>
                <tr>
                    <td valign="top">
                    <jsp:include page='<%= (String)request.getAttribute("Description") %>' flush='true' />
                    
                    </td>
                </tr>
<tr>
<td>
<br>
<br>
<div style="padding-left: 2em; padding-right: 2em">
    <form name="dmsubmitform" action="/downloadManager/download" method="POST">
        <c:forEach var="tuple" items="<%= tupleList %>">
            <input type="hidden" name="tuple" value="${dtFormat.format(tuple)}" />
        </c:forEach>

        <c:if test="<%= downloadReq.runID != null %>" >
            <input type="hidden" name="runid" value="${downloadReq.runID}" />
        </c:if>

        <c:if test="<%= skinURL != null %>" >
            <input type="hidden" name="skin" value="<%= skinURL %>" />
        </c:if>

        <input type="hidden" name="execute" value="Submit" />
    </form>
    
    <form name="clear" action="/downloadManager/download" method="POST">
        <c:forEach var="tuple" items="<%= tupleList %>">
            <input type="hidden" name="tuple" value="${dtFormat.format(tuple)}" />
        </c:forEach>

        <c:if test="<%= downloadReq.runID != null %>" >
            <input type="hidden" name="runid" value="${downloadReq.runID}" />
        </c:if>

        <c:if test="<%= skinURL != null %>" >
            <input type="hidden" name="skin" value="<%= skinURL %>" />
        </c:if>

        <input type="submit" name="clearCookie" value="Chose one of the other download methods" />
        <input type="submit" OnClick="closemyself()" value="Close window" />
    </form>
</div>
</td>
</tr>    
</table>
<c:catch><c:import url="<%= footerURL%>" /></c:catch>
<script type="text/javascript">
	document.dmsubmitform.submit();
</script>
</body>
</html>
