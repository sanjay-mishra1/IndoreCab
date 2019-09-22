package com.example.sanjay.cab.map;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sanjay.cab.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.StrictMode;
import android.util.Log;

public class GMapV2Direction {
      final static String MODE_DRIVING = "driving";
    public final static String MODE_WALKING = "walking";
    private Context context;
    private Document doc;

    GMapV2Direction(Context context) {
        this.context=context;
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

      Document getDocument(LatLng start, LatLng end, final XmlResourceParser key, final GoogleMap map) {
          final String[] response = new String[1];
        String url = "https://maps.googleapis.com/maps/api/directions/xml?key="+"AIzaSyBo1jT616ex45y5XrWJDErEpuyLiHvprX8"
                + "&origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving";
        Log.d("url", url);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            final HttpResponse responses = httpClient.execute(httpPost, localContext);


            StringRequest request=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                @Override
                public void onResponse(String respon) {
                    response[0]= respon;
                    Log.e("Response",respon);
                    DocumentBuilder builder = null;
                    try {
                        builder = DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder();
                        InputStream in = responses.getEntity().getContent();
                        char []a=new char[1];
                        InputStream in2 = new ByteArrayInputStream(giveXML().getBytes(Charset.forName("UTF-8")));
                          doc = builder.parse(in2);
                          createPolyLine(getDirection(),map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            Volley.newRequestQueue(context).add(request);





                   // InputStream in = response[0];//.getEntity().getContent();



             return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
     void createPolyLine(ArrayList directionPoint, GoogleMap map){
         //ArrayList<LatLng> directionPoint = md.getDirection();
         PolylineOptions rectLine = new PolylineOptions().width(5).color(
                 Color.BLACK);
        ArrayList<LatLng> arrayList=new ArrayList<>();
         for (int i = 0; i < directionPoint.size(); i++) {
             String s=directionPoint.get(i).toString();
             Log.e("CreatePolyline","s=>"+s);
             Double lat=Double.parseDouble( s.substring(s.indexOf("(")+1,s.indexOf(",")-1));
             Double lng=Double.parseDouble(s.substring(s.indexOf(",")+1,s.indexOf(")")-1));
             Log.e("CreatePolyLine",""+lat+"=>"+lng);
             //rectLine.add(new LatLng(lat,lng));
             arrayList.add(new LatLng(lat,lng));

         }
         rectLine.addAll(arrayList);
         Polyline polylin = map.addPolyline(rectLine);
     }
    public String getDurationText(Document doc) {
        try {

            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "text"));
            Log.i("DurationText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "0";
        }
    }

    public int getDurationValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DurationValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
    }

    public String getDistanceText(Document doc) {
        /*
         * while (en.hasMoreElements()) { type type = (type) en.nextElement();
         *
         * }
         */

        try {
            NodeList nl1;
            nl1 = doc.getElementsByTagName("distance");

            Node node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = null;
            nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.d("DistanceText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

        /*
         * NodeList nl1; if(doc.getElementsByTagName("distance")!=null){ nl1=
         * doc.getElementsByTagName("distance");
         *
         * Node node1 = nl1.item(nl1.getLength() - 1); NodeList nl2 = null; if
         * (node1.getChildNodes() != null) { nl2 = node1.getChildNodes(); Node
         * node2 = nl2.item(getNodeIndex(nl2, "value")); Log.d("DistanceText",
         * node2.getTextContent()); return node2.getTextContent(); } else return
         * "-1";} else return "-1";
         */
    }

    public int getDistanceValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("distance");
            Node node1 = null;
            node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DistanceValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
        /*
         * NodeList nl1 = doc.getElementsByTagName("distance"); Node node1 =
         * null; if (nl1.getLength() > 0) node1 = nl1.item(nl1.getLength() - 1);
         * if (node1 != null) { NodeList nl2 = node1.getChildNodes(); Node node2
         * = nl2.item(getNodeIndex(nl2, "value")); Log.i("DistanceValue",
         * node2.getTextContent()); return
         * Integer.parseInt(node2.getTextContent()); } else return 0;
         */
    }

    public String getStartAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("start_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }

    public String getEndAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("end_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }
    }
    public String getCopyRights(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("copyrights");
            Node node1 = nl1.item(0);
            Log.i("CopyRights", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }

    public ArrayList<LatLng> getDirection() {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        Log.e("Response","inside getDirection");
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2
                        .item(getNodeIndex(nl2, "start_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));

                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "points"));
                ArrayList<LatLng> arr = decodePoints(latNode.getTextContent());
                for (int j = 0; j < arr.size(); j++) {
                    listGeopoints.add(new LatLng(arr.get(j).latitude, arr
                            .get(j).longitude));
                }

                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "lat"));
                lat = Double.parseDouble(latNode.getTextContent());
                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));
                Log.e("Response",""+listGeopoints);
            }
        }

        return listGeopoints;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }
    private ArrayList<LatLng> decodePoints(String encoded){
        Log.e("DecodingPoints...",encoded);
    try {
        if (encoded.contains("\\"))
        encoded= encoded.replaceAll("\\\\","");
         Log.e("Encoded...",encoded);
        List<LatLng> decode=PolyUtil.decode(encoded);
        return (ArrayList<LatLng>) decode;
    }catch (Exception e){Log.e("Error",e.getMessage());}

        return new ArrayList<>();
    }
    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }
    String giveXML(){
        String xml="<DirectionsResponse>" +
                "<status>OK</status>" +
                "<route>" +
                "<summary>Eastern Ring Rd</summary>" +
                "<leg>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.6752127</lat>" +
                "<lng>75.8742249</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.6751531</lat>" +
                "<lng>75.8735057</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>awkiC{dbnM?\\?VBTBr@BL</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>17</value>" +
                "<text>1 min</text>" +
                "</duration>" +
                "<html_instructions>Head <b>west</b></html_instructions>" +
                "<distance>" +
                "<value>74</value>" +
                "<text>74 m</text>" +
                "</distance>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.6751531</lat>" +
                "<lng>75.8735057</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.6750498</lat>" +
                "<lng>75.8681771</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>" +
                "uvkiCm`bnM?@A@?@A@?@?@?@?B@@?B@D@FDP@LBPBP@L@N@L?B@\\AfAA`@CTAR?B?b@?r@CzA?J?J@RDj@?DBb@@DBX?F@F?HAX?p@Eh@A`@?@A`@Ch@?l@?B@`@?P@\\" +
                "</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>104</value>" +
                "<text>2 mins</text>" +
                "</duration>" +
                "<html_instructions>Slight <b>right</b></html_instructions>" +
                "<distance>" +
                "<value>550</value>" +
                "<text>0.6 km</text>" +
                "</distance>" +
                "<maneuver>turn-slight-right</maneuver>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.6750498</lat>" +
                "<lng>75.8681771</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.6803572</lat>" +
                "<lng>75.8673847</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>" +
                "avkiCc_anMwB?G?E@C?G@GBGBKHKFKHGHKHCBCBKDIDI@MBK@I?O@O?[?m@DwBPo@@g@DM@O@aFTyBD[@A?m@B" +
                "</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>121</value>" +
                "<text>2 mins</text>" +
                "</duration>" +
                "<html_instructions>Turn <b>right</b> onto <b>Ekta Nagar Rd</b></html_instructions>" +
                "<distance>" +
                "<value>605</value>" +
                "<text>0.6 km</text>" +
                "</distance>" +
                "<maneuver>turn-right</maneuver>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.6803572</lat>" +
                "<lng>75.8673847</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.6928212</lat>" +
                "<lng>75.8995034</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>" +
                "gwliCcz`nMQA?IC{AAUEUEQEKCIEIKOKMKKIIg@_@w@g@{B{AsA_AcD{BwAcAy@o@_@WU[SUSYYe@c@q@M[CEk@sAe@gA_@{@C?CCC?CCAACCACAC?CAC?C?C?E@C@EQk@Ic@Em@Aq@Bu@Fy@L_AxAcHJe@pAiHNu@Da@?O@WAa@Iu@COIe@Qg@ISO[[e@c@s@Yc@?AgC}Ds@cAYg@_@i@Y[MM[W[[_Aw@SMQMIGw@i@]U_@SYSGEeA{@sBuAk@_@UQQQSQU[U_@M]Oe@K_@Ga@Bo@@gA@q@ZkFHkAD{@N_D?G@ID{@FkABk@LgBNgCDoA?M@SCqAGqCKaC?EMgCKmAMaAIe@IYSi@KSWg@y@cBGKkAqBc@q@IIUW{@aA_AgAg@m@k@q@OMcA_A" +
                "</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>403</value>" +
                "<text>7 mins</text>" +
                "</duration>" +
                "<html_instructions>" +
                "Turn <b>right</b> onto <b>Eastern Ring Rd</b>/<b>Pipliya Rao Ring Rd</b><div style=\"font-size:0.9em\">Pass by ERP Implementation Company (on the left in 800&nbsp;m)</div>" +
                "</html_instructions>" +
                "<distance>" +
                "<value>3971</value>" +
                "<text>4.0 km</text>" +
                "</distance>" +
                "<maneuver>turn-right</maneuver>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.6928212</lat>" +
                "<lng>75.8995034</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.7046192</lat>" +
                "<lng>75.9058823</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>" +
                "ceoiC{bgnMA?A?A?E?A?C?C??AC?AAA?AAAAAAAAAAACAAAAACAC?A?CAC?A@C?C@CmAiBMO}@u@QMuCyBeBsASQUU}BkCu@_AIIIOaA_B}ByDk@eAIMoA}Be@y@c@q@W]]][Sa@Oa@Kc@GeEa@]CUA]?W?e@Bi@Bo@Fc@Fc@HcGnAgHtA" +
                "</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>172</value>" +
                "<text>3 mins</text>" +
                "</duration>" +
                "<html_instructions>" +
                "At the roundabout, take the <b>3rd</b> exit and stay on <b>Eastern Ring Rd</b><div style=\"font-size:0.9em\">Go past the petrol station (on the left)</div>" +
                "</html_instructions>" +
                "<distance>" +
                "<value>1659</value>" +
                "<text>1.7 km</text>" +
                "</distance>" +
                "<maneuver>roundabout-left</maneuver>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.7046192</lat>" +
                "<lng>75.9058823</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.7059001</lat>" +
                "<lng>75.9054320</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>{nqiCwjhnMs@T_@Fk@Lu@LiA^</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>36</value>" +
                "<text>1 min</text>" +
                "</duration>" +
                "<html_instructions>" +
                "Keep <b>left</b> to continue towards <b>Service Rd</b>" +
                "</html_instructions>" +
                "<distance>" +
                "<value>150</value>" +
                "<text>0.2 km</text>" +
                "</distance>" +
                "<maneuver>keep-left</maneuver>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.7059001</lat>" +
                "<lng>75.9054320</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.7083854</lat>" +
                "<lng>75.9054217</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>{vqiC}ghnMSEi@GS@}BFiADiA@q@@w@Cc@?</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>74</value>" +
                "<text>1 min</text>" +
                "</duration>" +
                "<html_instructions>Continue onto <b>Service Rd</b></html_instructions>" +
                "<distance>" +
                "<value>277</value>" +
                "<text>0.3 km</text>" +
                "</distance>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.7083854</lat>" +
                "<lng>75.9054217</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.7100795</lat>" +
                "<lng>75.9057394</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>mfriC{ghnM_@Ki@Kq@Is@C}@Ei@C]E[I</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>20</value>" +
                "<text>1 min</text>" +
                "</duration>" +
                "<html_instructions>Slight <b>right</b> towards <b>Eastern Ring Rd</b></html_instructions>" +
                "<distance>" +
                "<value>192</value>" +
                "<text>0.2 km</text>" +
                "</distance>" +
                "<maneuver>turn-slight-right</maneuver>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.7100795</lat>" +
                "<lng>75.9057394</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.7197550</lat>" +
                "<lng>75.9060481</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>" +
                "_qriC{ihnMqACU?{BCwA@gBJoCJ{BHy@BW?U?WAYEkASgCa@{@O]Ea@Ee@AM?}LIwAAgBDcAB" +
                "</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>128</value>" +
                "<text>2 mins</text>" +
                "</duration>" +
                "<html_instructions>Continue straight onto <b>Eastern Ring Rd</b></html_instructions>" +
                "<distance>" +
                "<value>1082</value>" +
                "<text>1.1 km</text>" +
                "</distance>" +
                "<maneuver>straight</maneuver>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.7197550</lat>" +
                "<lng>75.9060481</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.7283708</lat>" +
                "<lng>75.9331791</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>" +
                "omtiCykhnMEDC@E@C@GAEAEACCCCAEAGAE@E@G@CBEFEDU@ERa@BYL{@lA_HPq@FS?aAEU?AG]AEKg@o@aDI_@EMEUc@iBUcAGYOi@i@{Aw@}BUm@s@oBg@qAQa@eBmEY}@i@}AWu@c@kAEQUw@i@kBg@mAKSQWQUk@q@g@m@?Ai@s@a@q@c@w@eAgBa@o@KSIOSe@Qg@]eAMg@GSqA}E]sAaBoGeA{D[sAE]?CEc@Cs@?c@?a@GmBCm@As@AUIsEGsBEsACa@E_@CEEOEMg@sAKYgCyGi@kAEM" +
                "</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>394</value>" +
                "<text>7 mins</text>" +
                "</duration>" +
                "<html_instructions>" +
                "At <b>Bengali Square</b>, take the <b>3rd</b> exit onto <b>Garipipliya Rd</b>/<b>Kanadia Main Rd</b><div style=\"font-size:0.9em\">Pass by Karnawat Bhojnalaya (on the left in 350&nbsp;m)</div>" +
                "</html_instructions>" +
                "<distance>" +
                "<value>3073</value>" +
                "<text>3.1 km</text>" +
                "</distance>" +
                "<maneuver>roundabout-left</maneuver>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.7283708</lat>" +
                "<lng>75.9331791</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.7482386</lat>" +
                "<lng>75.9346511</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>" +
                "icviCkumnMC?sJcAyDa@_@AwFg@yCGiAAmQYy@CeQYoFOsS[eWg@" +
                "</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>237</value>" +
                "<text>4 mins</text>" +
                "</duration>" +
                "<html_instructions>Turn <b>left</b></html_instructions>" +
                "<distance>" +
                "<value>2217</value>" +
                "<text>2.2 km</text>" +
                "</distance>" +
                "<maneuver>turn-left</maneuver>" +
                "</step>" +
                "<step>" +
                "<travel_mode>DRIVING</travel_mode>" +
                "<start_location>" +
                "<lat>22.7482386</lat>" +
                "<lng>75.9346511</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.7482705</lat>" +
                "<lng>75.9371095</lng>" +
                "</end_location>" +
                "<polyline>" +
                "<points>o_ziCq~mnMyBqL?OBODMJG|AC</points>" +
                "</polyline>" +
                "<duration>" +
                "<value>88</value>" +
                "<text>1 min</text>" +
                "</duration>" +
                "<html_instructions>Turn <b>right</b></html_instructions>" +
                "<distance>" +
                "<value>317</value>" +
                "<text>0.3 km</text>" +
                "</distance>" +
                "<maneuver>turn-right</maneuver>" +
                "</step>" +
                "<duration>" +
                "<value>1794</value>" +
                "<text>30 mins</text>" +
                "</duration>" +
                "<distance>" +
                "<value>14167</value>" +
                "<text>14.2 km</text>" +
                "</distance>" +
                "<start_location>" +
                "<lat>22.6752127</lat>" +
                "<lng>75.8742249</lng>" +
                "</start_location>" +
                "<end_location>" +
                "<lat>22.7482705</lat>" +
                "<lng>75.9371095</lng>" +
                "</end_location>" +
                "<start_address>" +
                "283a, Khandwa Naka, Sant Nagar, Indore, Madhya Pradesh 452020, India" +
                "</start_address>" +
                "<end_address>" +
                "Hingoniya Rd, County Walk Twp, Indore, Madhya Pradesh 453771, India" +
                "</end_address>" +
                "</leg>" +
                "<copyrights>Map data ©2019 Google</copyrights>" +
                "<overview_polyline>" +
                "<points>" +
                "awkiC{dbnM?t@FhA@R@VLx@H|@@`@ChBEpACfDJhBFn@Ab@EzAGnB@dB@\\wB?M@[HkA|@SFYDeA@eDVwAF]B{IZ}ABCeBGk@Uq@m@s@oHcFuIgGwAqBq@mAo@yAeAcCGCMIEO@Q@EQk@OqA@gBTyBdBiI`B_JDq@?y@MeA[mAYo@_AyAuEgHy@qAg@i@kCyBqBuAy@g@mAaA_DuBg@c@i@m@c@}@[eAGa@Bo@ByBd@wHVmFLgCPsCTwE@a@KcFKgCYuEWgB]cAc@{@aAoBoBcD{CkDsA_BsAmAE?O?OKKS?M@G{AyBkIqGi@g@sDkEsEsHkDkG{@oA]][ScA[iFi@s@Eu@?oAFsANgHxAgHtAs@TkATu@LiA^}@MqCHsCFiBAc@?_@K{AUqBIgAI[IqACqCCwA@gBJkGTqABm@AiHkA_AKs@AuOKkDHIFIBMCIEIWHWFEDUTg@PuAlA_HPq@FS?aAEWeAmFy@mD]}Ay@eCmAkD{AaEwBoFcA{CwAkEi@kBg@mA]k@eBwBkAeBiB_Dm@cA]u@o@mBU{@oBqHgDkMa@qBEg@KgFGwBQgIIuBIe@K]s@mBqDeJEMC?mPeBwGi@cFIgS]uXi@sS[eWg@yBqL?OH]JG|AC" +
                "</points>" +
                "</overview_polyline>" +
                "<bounds>" +
                "<southwest>" +
                "<lat>22.6749946</lat>" +
                "<lng>75.8673860</lng>" +
                "</southwest>" +
                "<northeast>" +
                "<lat>22.7488469</lat>" +
                "<lng>75.9371095</lng>" +
                "</northeast>" +
                "</bounds>" +
                "</route>" +
                "<geocoded_waypoint>" +
                "<geocoder_status>OK</geocoder_status>" +
                "<type>street_address</type>" +
                "<place_id>ChIJTz4yq5X8YjkRQWf1A8uRmao</place_id>" +
                "</geocoded_waypoint>" +
                "<geocoded_waypoint>" +
                "<geocoder_status>OK</geocoder_status>" +
                "<type>route</type>" +
                "<place_id>ChIJlZKSOoPiYjkR0FblovaBKzU</place_id>" +
                "</geocoded_waypoint>" +
                "</DirectionsResponse>";
        return xml;
    }
}
