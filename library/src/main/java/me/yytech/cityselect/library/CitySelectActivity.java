package me.yytech.cityselect.library;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class CitySelectActivity extends ActionBarActivity implements StickyListHeadersListView.OnStickyHeaderChangedListener {

    private StickyListHeadersListView mStickyListHeadersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mStickyListHeadersListView = (StickyListHeadersListView) findViewById(R.id.lvCity);
        mStickyListHeadersListView.setOnStickyHeaderChangedListener(this);
        mStickyListHeadersListView.setEmptyView(findViewById(R.id.empty_view));

        QuickScroll quickScroll = (QuickScroll)findViewById(R.id.quickscroll);

        initData();
    }

    private void initData() {
        new AsyncTask<Void, Void, Void>() {

            ArrayList<String> strings = new ArrayList<String>();
            ArrayList<Character> letters = new ArrayList<Character>();

            @Override
            protected Void doInBackground(Void... params) {
                XmlResourceParser xrp = getResources().getXml(R.xml.cities);
                try {
                    while (xrp.next() != XmlResourceParser.START_TAG) {
                        continue;
                    }
                    xrp.next();
                    int readCount = 0;
                    while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                        while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                            if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                                return null;
                            }
                            xrp.next();
                        }
                        if (xrp.getName().equals("City")) {
                            String s = xrp.nextText();
                            strings.add(s);
                            if (s.length() > 0) {
                                letters.add(PinyinHelper.toHanyuPinyinStringArray(s.charAt(0))[0].charAt(0));
                            }
                        }
                        while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                            xrp.next();
                        }
                        xrp.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (!isFinishing()) {
                    String[] stringList = strings.toArray(new String[strings.size()]);
                    Arrays.sort(stringList, new CityComparator());
                    Character[] letterArray = letters.toArray(new Character[letters.size()]);
                    Arrays.sort(letterArray, new CharacterComparator());
                    CityAdapter adapter = new CityAdapter(stringList, letterArray);
                    mStickyListHeadersListView.setAdapter(adapter);
                }
            }
        }.execute();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
        header.setAlpha(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CityAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private String[] mStringList;
        private Character[] mStringLetters;

        public CityAdapter(String[] pStringList, Character[] pStringLetters) {
            mStringList = pStringList;
            mStringLetters = pStringLetters;
        }

        @Override
        public int getCount() {
            if (mStringList == null) {
                return 0;
            }
            return mStringList.length;
        }

        @Override
        public Object getItem(int position) {
            if (mStringList == null) {
                return null;
            }
            return mStringList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(CitySelectActivity.this).inflate(R.layout.cityselect_item_city, null);
            }
            TextView textView = (TextView) convertView;
            textView.setText((CharSequence) getItem(position));
            return convertView;
        }

        @Override
        public View getHeaderView(int i, View pView, ViewGroup pViewGroup) {
            if (pView == null) {
                pView = LayoutInflater.from(CitySelectActivity.this).inflate(R.layout.cityselect_item_city_header, null);
            }
            TextView textView = (TextView) ((ViewGroup) pView).getChildAt(0);
            textView.setText(mStringLetters[i].toString().toUpperCase());
            return pView;
        }

        @Override
        public long getHeaderId(int i) {
            return (mStringLetters[i]).charValue();
        }
    }

    public static void startCitySelect(Activity pActivity, int requestCode) {
        Intent intent = new Intent(pActivity, CitySelectActivity.class);
        pActivity.startActivityForResult(intent, requestCode);
    }
}
