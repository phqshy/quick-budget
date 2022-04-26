package me.fishy.testapp.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import me.fishy.testapp.R;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private int CURRENTVIEW = 0;

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
            CURRENTVIEW = 1;
        });
        findViewById(R.id.location_home).setOnClickListener(v -> {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            navHostFragment.getNavController().navigate(R.id.action_exchangeRateFragment_to_homeFragment);
            drawer.closeDrawer(GravityCompat.START);
            CURRENTVIEW = 0;
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == (int) 16908332) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
}