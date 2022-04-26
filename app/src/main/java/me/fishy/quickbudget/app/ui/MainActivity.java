package me.fishy.quickbudget.app.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;

import me.fishy.testapp.R;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    public static Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.mainActionBar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_hamburger));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        drawer = findViewById(R.id.drawer_layout);
        findViewById(R.id.location_exchange).setOnClickListener(v -> {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            navHostFragment.getNavController().navigate(R.id.action_homeFragment_to_exchangeRateFragment);
            drawer.closeDrawer(GravityCompat.START);
        });
        findViewById(R.id.location_home).setOnClickListener(v -> {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            navHostFragment.getNavController().navigate(R.id.action_exchangeRateFragment_to_homeFragment);
            drawer.closeDrawer(GravityCompat.START);
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == (int) 16908332) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        /*if (item.getItemId() == R.id.action_settings){
            Toast.makeText(getBaseContext(), "Sending data to webserver", Toast.LENGTH_SHORT).show();
            UserDataHolder data = new UserDataHolder("Fishy", "dE3zNUTs");
            try {
                new JSONPostRequest("http://159.223.120.100:1984/updateuser").post(gson.toJson(data))
                .thenAcceptAsync(System.out::println);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }*/
        return false;
    }
}