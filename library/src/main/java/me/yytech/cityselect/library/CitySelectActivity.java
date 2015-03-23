package me.yytech.cityselect.library;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.stuxuhai.jpinyin.PinyinHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class CitySelectActivity extends ActionBarActivity implements StickyListHeadersListView.OnStickyHeaderOffsetChangedListener, StickyListHeadersListView.OnStickyHeaderChangedListener {

    private StickyListHeadersListView mStickyListHeadersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);
        mStickyListHeadersListView = (StickyListHeadersListView) findViewById(R.id.lvCity);
        mStickyListHeadersListView.setOnStickyHeaderChangedListener(this);
        mStickyListHeadersListView.setOnStickyHeaderOffsetChangedListener(this);


        ArrayList<String> strings = new ArrayList<String>();
        ArrayList<Character> letters = new ArrayList<Character>();
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
                        return;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals("City")) {
                    String s = xrp.nextText();
                    strings.add(s);
                    letters.add(Character.valueOf(PinyinHelper.getShortPinyin(s).charAt(0)));
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

        String[] stringList = strings.toArray(new String[strings.size()]);
        Arrays.sort(stringList, new CityComparator());
        Character[] letterArray = letters.toArray(new Character[letters.size()]);
        Arrays.sort(letterArray, new CharacterComparator());
        CityAdapter adapter = new CityAdapter(stringList, letterArray);
        mStickyListHeadersListView.setAdapter(adapter);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            header.setAlpha(1 - (offset / (float) header.getMeasuredHeight()));
        }
    }


    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
        header.setAlpha(1);
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
            TextView textView = (TextView) pView;
            textView.setText(mStringLetters[i].toString());
            return pView;
        }

        @Override
        public long getHeaderId(int i) {
            return i;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_city_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void startCitySelect(Activity pActivity, int requestCode) {
        Intent intent = new Intent(pActivity, CitySelectActivity.class);
        pActivity.startActivityForResult(intent, requestCode);
    }
}
