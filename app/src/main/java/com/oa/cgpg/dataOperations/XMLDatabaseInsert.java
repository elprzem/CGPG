package com.oa.cgpg.dataOperations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.oa.cgpg.models.buildingEntity;
import com.oa.cgpg.models.opinionNetEntity;
import com.oa.cgpg.models.poiEntity;
import com.oa.cgpg.models.typeEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tomasz on 2014-11-07.
 */
public class XMLDatabaseInsert extends AsyncTask<Void, Void, Void> {
    private static final String HTTP_URL = "http://cgpg1.zz.mu/webservice1.php?update=1";
    private String xml;
    private ProgressDialog progressDialog;
    private Context context;
    public AsyncResponse delegate = null;
    private dbOps dbOps;
    private static final String ns = null;
    private List<buildingEntity> listBuildings = null;
    private List<typeEntity> listTypes = null;
    private List<poiEntity> listPois = null;

    public XMLDatabaseInsert(Context c, dbOps d) {
        this.context = c;
        this.dbOps = d;
        listBuildings = new ArrayList<buildingEntity>();
        listTypes = new ArrayList<typeEntity>();
        listPois = new ArrayList<poiEntity>();
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "Pobierane danych", "Proszę czekać...", true, false);
    }

    @Override
    protected void onPostExecute(Void D) {
        progressDialog.dismiss();
        //  delegate.processFinish(xml);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // defaultHttpClient
            Log.i(getClass().getName(), "Create client");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            Log.i(getClass().getName(), "Create request");
            HttpPost httpPost = new HttpPost(HTTP_URL);
            Log.i(getClass().getName(), "Execute request");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.i(getClass().getName(), "Get response");
            HttpEntity httpEntity = httpResponse.getEntity();
            Log.i(getClass().getName(), "Parse xml");
            xml = EntityUtils.toString(httpEntity, HTTP.UTF_8);
            Log.i(getClass().getName(), "xml length " + xml.length());
            checkVersion();
        } catch (UnsupportedEncodingException e) {
            Log.e(getClass().getName(), "UnsupportedEncodingException");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e(getClass().getName(), "ClientProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(getClass().getName(), "IOException");
            e.printStackTrace();
        }
        return null;
    }

    public String getXmlFromUrl() {
        return xml;
    }

    private void checkVersion() {
        try {
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            int version = parseVersion(is);
            // if (version != dbOps.getVersion().getVersionNumber()){
            is.reset();
            dbOps.update();
            dbOps.changeVersion(version);
            getList(is);
            // }

            Log.i("VERSION:", version + "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public void getList(InputStream is) {
        try {
            parse(is);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public int parseVersion(InputStream in) throws XmlPullParserException, IOException, ParseException {

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        parser.nextTag();
        return readVersion(parser);

    }

    public void parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readXML(parser);
        } finally {
            in.close();
        }
    }

    private int readVersion(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        int version = 0;
        parser.require(XmlPullParser.START_TAG, ns, "cgpg");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("version")) {
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String nodeName = parser.getName();
                    if (nodeName.equals("number")) {
                        version = readInt(parser, "number");
                    } else {
                        skip(parser);
                    }
                }
            } else {
                skip(parser);
            }
        }
        return version;
    }


    private void readXML(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "cgpg");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("buildings")) {
                readBuildings(parser);
            } else if (name.equals("types")) {
                readTypes(parser);
            } else if (name.equals("pois")) {
                readPois(parser);
            } else {
                skip(parser);
            }
        }
    }

    private void readBuildings(XmlPullParser parser) throws XmlPullParserException, IOException,
            ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "buildings");

        int id = 0;
        String name = null;
        String description = null;
        int x1 = 0;
        int y1 = 0;
        int x2 = 0;
        int y2 = 0;
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nName = parser.getName();
            // Starts by looking for the entry tag
            if (nName.equals("building")) {
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String nodeName = parser.getName();
                    if (nodeName.equals("id")) {
                        id = readInt(parser, "id");
                    } else if (nodeName.equals("x1")) {
                        x1 = readInt(parser, "x1");
                    } else if (nodeName.equals("x2")) {
                        x2 = readInt(parser, "x2");
                    } else if (nodeName.equals("x3")) {
                        x3 = readInt(parser, "x3");
                    } else if (nodeName.equals("x4")) {
                        x4 = readInt(parser, "x4");
                    } else if (nodeName.equals("y1")) {
                        y1 = readInt(parser, "y1");
                    } else if (nodeName.equals("y2")) {
                        y2 = readInt(parser, "y2");
                    } else if (nodeName.equals("y3")) {
                        y3 = readInt(parser, "y3");
                    } else if (nodeName.equals("y4")) {
                        y4 = readInt(parser, "y4");
                    } else if (nodeName.equals("name")) {
                        name = readString(parser, "name");
                    } else if (nodeName.equals("description")) {
                        description = readString(parser, "description");
                    } else if (nodeName.equals("link")) {
                        link = readString(parser, "link");
                    } else {
                        skip(parser);
                    }
                }
                listBuildings.add(new buildingEntity(id, name, description, x1, y1, x2, y2, x3, y3, x4, y4, link));
            } else {
                skip(parser);
            }
        }

        for (buildingEntity b : listBuildings) {
            dbOps.commitBuilding(b);
        }
    }

    private void readTypes(XmlPullParser parser) throws XmlPullParserException, IOException,
            ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "types");

        int id = 0;
        String name = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nName = parser.getName();
            // Starts by looking for the entry tag
            if (nName.equals("type")) {
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String nodeName = parser.getName();
                    if (nodeName.equals("id")) {
                        id = readInt(parser, "id");
                    } else if (nodeName.equals("name")) {
                        name = readString(parser, "name");
                    } else {
                        skip(parser);
                    }
                }
                listTypes.add(new typeEntity(id, name));
            } else {
                skip(parser);
            }
        }
        for (typeEntity t : listTypes) {
            dbOps.commitType(t);
        }
    }


    private void readPois(XmlPullParser parser) throws XmlPullParserException, IOException,
            ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "pois");

        int id = 0;
        String name = null;
        String description = null;
        int buildingKey = 0;
        int typeKey = 0;
        int ratingPlus = 0;
        int ratingMinus = 0;
        String link = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nName = parser.getName();
            // Starts by looking for the entry tag
            if (nName.equals("poi")) {
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String nodeName = parser.getName();
                    if (nodeName.equals("id")) {
                        id = readInt(parser, "id");
                    } else if (nodeName.equals("name")) {
                        name = readString(parser, "name");
                    } else if (nodeName.equals("description")) {
                        description = readString(parser, "description");
                    } else if (nodeName.equals("buildingKey")) {
                        buildingKey = readInt(parser, "buildingKey");
                    } else if (nodeName.equals("typeKey")) {
                        typeKey = readInt(parser, "typeKey");
                    } else if (nodeName.equals("ratingPlus")) {
                        ratingPlus = readInt(parser, "ratingPlus");
                    } else if (nodeName.equals("ratingMinus")) {
                        ratingMinus = readInt(parser, "ratingMinus");
                    } else if (nodeName.equals("link")) {
                        link = readString(parser, "link");
                    } else {
                        skip(parser);
                    }
                }
                buildingEntity b = dbOps.getBuildingById(buildingKey);
                typeEntity t = dbOps.getTypeById(typeKey);
                listPois.add(new poiEntity(id, name, b, description, t, ratingPlus, ratingMinus, link));
            } else {
                skip(parser);
            }
        }
        for (poiEntity p : listPois) {
            dbOps.commitPOI(p);
        }
    }

    private int readInt(XmlPullParser parser, String nodeName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, nodeName);
        int tmp = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, nodeName);
        return tmp;
    }

    private String readString(XmlPullParser parser, String nodeName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, nodeName);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, nodeName);
        return text;
    }


    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
