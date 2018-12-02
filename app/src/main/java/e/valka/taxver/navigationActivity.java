package e.valka.taxver;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.PendingResult.Callback;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import e.valka.taxver.Fragments.ConductorFragment;
import e.valka.taxver.Fragments.PlaceAutocompleteAdapter;
import e.valka.taxver.Models.Conductor;
import e.valka.taxver.Models.Posicionconductor;
import e.valka.taxver.Models.Usuarios;
import e.valka.taxver.Notifications.Azure.MyHandler;
import e.valka.taxver.Notifications.Azure.NotificationSettings;
import e.valka.taxver.Notifications.Azure.RegistrationIntentService;
import e.valka.taxver.Utils.DownloadAsyncTask;
import e.valka.taxver.Utils.URLS;

import static e.valka.taxver.Login.KEY_DEVICEID;
import static e.valka.taxver.Login.KEY_EMAIL;
import static e.valka.taxver.Login.KEY_PASSWORD;
import static e.valka.taxver.Login.MY_PREFERENCES;

public class navigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,Serializable, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private  Usuarios usuario;
    private  Conductor con;
    private AutoCompleteTextView buscar;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    public static final String TAG = "PEKA";
    public static navigationActivity mainActivity;
    public static Boolean isVisible = true;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final float DEFAULT_ZOOM = 18f;
    protected GeoDataClient mGeoDataClient;
    private GoogleApiClient mGoogleApiClient;
    private GeoApiContext mGeoApiContext = null;
    SharedPreferences sharedPreferences;
    RelativeLayout buscarLayout;
    FloatingActionButton fab;
    FloatingActionButton fabact;
    FloatingActionButton fabcon;
    FloatingActionButton fabclose;
    static ArrayList<Marker> markers = new ArrayList<>();
    private boolean seleccionar = false;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(19.061394, -96.210008), new LatLng(19.198214, -96.087768));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuario = (Usuarios) getIntent().getSerializableExtra("usuario");
        setContentView(R.layout.activity_navigation);

        sharedPreferences = getSharedPreferences (MY_PREFERENCES, Context.MODE_PRIVATE);
        NotificationsManager.handleNotifications (this, NotificationSettings.SenderId, MyHandler.class);
        registerWithNotificationHubs();
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);
        buscarLayout = findViewById(R.id.relLayout1);
        buscar = (AutoCompleteTextView) findViewById(R.id.input_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_api_key)).build();
        fab =  findViewById(R.id.fabtax);
        fabact =  findViewById(R.id.fabact);
        fabcon =  findViewById(R.id.fabcon);
        fabclose =  findViewById(R.id.fabstop);
        if(usuario.IdTipoUsuario != 2){
            fabclose.hide();
            fabact.hide();
            fabcon.hide();
            fab.setOnClickListener((v)->{
                v.setVisibility(View.GONE);
                Toast.makeText (getBaseContext (), "Selecciona lugar de origen", Toast.LENGTH_LONG).show ();
                seleccionar = true;
                fabact.show();
            });
            fabact.setOnClickListener((v)->{
                if(markers.size() == 1){
                    v.setVisibility(View.GONE);
                    seleccionar = false;
                    Toast.makeText (getBaseContext (), "Selecciona tu destino", Toast.LENGTH_LONG).show ();
                    buscarLayout.setVisibility(View.VISIBLE);
                    fabcon.show();
                }else{
                    Toast.makeText (getBaseContext (), "Seleccionar lugar de origen", Toast.LENGTH_LONG).show ();
                }
            });
            fabcon.setOnClickListener((v) -> {
                if(markers.size() == 2){
                    v.setVisibility(View.GONE);
                    //calculateDirections(markers.get(0),markers.get(1));
                    showEditDialog();
                }else{
                    Toast.makeText (getBaseContext (), "Selecciona tu destino", Toast.LENGTH_LONG).show ();
                }
            });
        }else{
            fab.hide();
            fabcon.hide();
            fabclose.hide();
            fabact.setOnClickListener((v)->{
                new DownloadAsyncTask(j ->{
                    fabclose.show();
                    v.setVisibility(View.GONE);
                    }).execute (URLS.statusPosicon+"?idConductor="+con.IdConductor);

            });
            fabclose.setOnClickListener((v)->{
                new DownloadAsyncTask(j ->{
                    fabact.show();
                    v.setVisibility(View.GONE);
                }).execute (URLS.statusPosicon+"?idConductor="+con.IdConductor);
            });
        }

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mGeoDataClient = Places.getGeoDataClient(this, null);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient,LAT_LNG_BOUNDS,null);
        buscar.setAdapter(placeAutocompleteAdapter);
        buscar.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_SEARCH
                    || i == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){


                hideSoftKeyboard();
                geoLocate();
            }
            return  false;
        });
        buscar.setOnItemClickListener(mAutocompleteClickListener);
        Window window = this.getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View header = navigationView.getHeaderView(0);
        TextView nombre = header.findViewById(R.id.Nombre);
        if(usuario.IdPersonaNavigation.ApellidoMaterno != null)
            nombre.setText(usuario.IdPersonaNavigation.Nombre + " "+usuario.IdPersonaNavigation.ApellidoPaterno +" "+usuario.IdPersonaNavigation.ApellidoMaterno);
        else
            nombre.setText(usuario.IdPersonaNavigation.Nombre + " "+usuario.IdPersonaNavigation.ApellidoPaterno);
        buscarLayout.setVisibility(View.GONE);
        mainActivity = this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void geoLocate(){
        hideSoftKeyboard();
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = buscar.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.e(TAG, "Address: "+address.getAddressLine(0));
            LatLng addressLatLng = new LatLng(address.getLatitude(),address.getLongitude());
            if(markers.size() == 2 ){
                markers.get(1).remove();
                markers.remove(1);
            }
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(addressLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(address.getAddressLine(0)));
            markers.add(marker);
            mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(addressLatLng, DEFAULT_ZOOM)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent viajes = new Intent(this, Viajes.class);
            viajes.putExtra("IDPERSONA",usuario.IdPersona);
            startActivity(viajes);
        } else if (id == R.id.nav_gallery) {
            Intent objetos = new Intent(this, e.valka.taxver.objetos.class);
            objetos.putExtra("IDPERSONA",usuario.IdPersona);
            startActivity(objetos);

        } else if (id == R.id.nav_manage) {

        }else if (id == R.id.logout){
            SharedPreferences.Editor editor = sharedPreferences.edit ();
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_PASSWORD);
            editor.remove(KEY_DEVICEID);
            editor.apply();
            this.finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener((latLng)->{
            if(seleccionar){
                if(markers.size() == 1){
                    markers.get(0).remove();
                    markers.remove(0);
                }
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title("Origen"));
                markers.add(marker);
            }
        });
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }
        moverAPosicion();

    }
    void moverAPosicion(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }else{
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),DEFAULT_ZOOM));
            if(usuario.IdTipoUsuario == 2){
                new DownloadAsyncTask(j ->{
                    con = parseJSONConductor(j);
                    new DownloadAsyncTask(s ->{
                    }).execute (URLS.ActualizarPosicion+"?lat="+Double.toString(lat)+"&lng="+Double.toString(lng)+"&idConductor="+con.IdConductor);
                }).execute (URLS.Conductor+"?correo="+usuario.Nombre);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    moverAPosicion();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void sendPost(Posicionconductor pos) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(URLS.ActualizarPosicion);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    JSONObject jsonParam = new JSONObject(new Gson().toJson(pos));

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    private Conductor parseJSONConductor (String json) {
        Conductor conductor = new Gson().fromJson (json, Conductor.class);
        if (conductor == null) return null;
        return conductor;
    }
    private void showEditDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new ConductorFragment();
        dialogFragment.show(ft, "dialog");
    }
    public void registerWithNotificationHubs ()
        {
        if (checkPlayServices ()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent (this, RegistrationIntentService.class);
            startService (intent);
        }
    }

    private boolean checkPlayServices () {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance ();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable (this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show ();
            } else {
                Log.i ("PEKKA", "This device is not supported by Google Play Services.");
                ToastNotify ("This device is not supported by Google Play Services.");
                finish ();
            }

            return false;
        }

        return true;
    }
    @Override
    protected void onStart () {
        super.onStart ();
        isVisible = true;
    }

    @Override
    protected void onPause () {
        super.onPause ();
        isVisible = false;
    }

    @Override
    protected void onResume () {
        super.onResume ();
        isVisible = true;
    }

    @Override
    protected void onStop () {
        super.onStop ();
        isVisible = false;
    }
    public void ToastNotify (final String notificationMessage) {
        runOnUiThread (() -> {
            Log.i("PEKKA","MIRA MAMA NO LLEGUÉ!: "+usuario.PhoneId +"POR: "+notificationMessage);
            if(notificationMessage.equals(usuario.PhoneId)){
                if(usuario.IdTipoUsuario == 2){
                    Toast.makeText (getBaseContext (), "ESTAS EN VIAJE!", Toast.LENGTH_LONG).show ();
                    fabact.hide();
                    fabclose.hide();
                }
            }
        });
    }
    public String getID(){
        return usuario.PhoneId;
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            LatLng latLng = new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
            if(markers.size() == 2){
                markers.get(1).remove();
                markers.remove(1);
            }
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(place.getAddress().toString()));
            markers.add(marker);
            places.release();
        }
    };
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void calculateDirections(Marker markerOrigen, Marker markerDestino){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                markerDestino.getPosition().latitude,
                markerDestino.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(false);
        directions.origin(
                new com.google.maps.model.LatLng(
                        markerOrigen.getPosition().latitude,
                        markerOrigen.getPosition().longitude
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(  new Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: Distance: " + result.routes[0].legs[0].distance);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }

        });
    }
    public  void pedir(int idConductor){
        new DownloadAsyncTask(s ->{

        }).execute (URLS.Pedir+"?idCondcutor="+idConductor+"&idPersona="+usuario.IdPersona+"&LatO="+markers.get(0).getPosition().latitude+"&LngO="+markers.get(0).getPosition().longitude+"&LatD="+markers.get(1).getPosition().latitude+"&LngD="+markers.get(1).getPosition().longitude);
    }

}

