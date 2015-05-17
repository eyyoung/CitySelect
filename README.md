# CitySelect
Select City Data

## Use
Add Bintray Repositories
<pre>
allprojects {
    repositories {
        jcenter()
        maven {
            url "http://dl.bintray.com/eyyoung/maven"
        }
    }
}
</pre>

Add dependency
<pre>
compile 'me.yytech.android.CitySelect:library:1.0.+@aar'
</pre>

### View

<pre>
<me.yytech.cityselect.library.CityListView
        android:id="@+id/vCity"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</pre>

### Activity

<pre>
    public void test(View view) {
        CitySelectActivity.startCitySelect(this, 0x1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        String city = data.getStringExtra(CitySelectActivity.RESULT_CITY);
        Toast.makeText(this, city, Toast.LENGTH_LONG).show();
    }
</pre>