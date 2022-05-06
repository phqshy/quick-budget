package me.fishy.testapp.app.ui.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.fragment.NavHostFragment;

import java.net.MalformedURLException;

import me.fishy.testapp.R;
import me.fishy.testapp.app.ui.fragment.PaymentsAddFragment;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.JSONPostRequest;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;

    //0 = home, 1 = exchange, 2 = payments
    private int menuMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.mainActionBar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_hamburger));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        switch (menuMode){
            case 0:
                getMenuInflater().inflate(R.menu.toolbar_menu, menu);
                initDrawer();
                return true;
            case 1:
                getMenuInflater().inflate(R.menu.toolbar_info, menu);
                initDrawer();
                return true;
            case 2:
                getMenuInflater().inflate(R.menu.toolbar_payment, menu);
                initDrawer();
                return true;
            default:
                getMenuInflater().inflate(R.menu.toolbar_menu, menu);
                initDrawer();
                return true;
        }
    }

    private void initDrawer(){
        drawer = findViewById(R.id.drawer_layout);
        findViewById(R.id.location_exchange).setOnClickListener(v -> {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            switch (menuMode){
                case 0:
                    navHostFragment.getNavController().navigate(R.id.action_homeFragment_to_exchangeRateFragment);
                    break;
                case 2:
                    navHostFragment.getNavController().navigate(R.id.action_paymentsFragment_to_exchangeRateFragment);
                    getSupportActionBar().setTitle("Quick Budget");
                    break;
                default:
                    return;
            }

            //set menu to exchange
            menuMode = 1;
            invalidateOptionsMenu();
            drawer.closeDrawer(GravityCompat.START);
        });
        findViewById(R.id.location_home).setOnClickListener(v -> {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            switch (menuMode){
                case 1:
                    navHostFragment.getNavController().navigate(R.id.action_exchangeRateFragment_to_homeFragment);
                    break;
                case 2:
                    navHostFragment.getNavController().navigate(R.id.action_paymentsFragment_to_homeFragment);
                    getSupportActionBar().setTitle("Quick Budget");
                    break;
                default:
                    return;
            }

            //set menu to home
            menuMode = 0;
            invalidateOptionsMenu();
            drawer.closeDrawer(GravityCompat.START);
        });
        findViewById(R.id.location_payment).setOnClickListener(v -> {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            switch (menuMode){
                case 0:
                    navHostFragment.getNavController().navigate(R.id.action_homeFragment_to_paymentsFragment);
                    break;
                case 1:
                    navHostFragment.getNavController().navigate(R.id.action_exchangeRateFragment_to_paymentsFragment);
                    break;
                default:
                    return;
            }

            //set menu to payments
            menuMode = 2;
            invalidateOptionsMenu();
            drawer.closeDrawer(GravityCompat.START);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == (int) 16908332) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        switch (item.getItemId()) {
            case 16908332:
                drawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_info:
                PopupWindow window = new PopupWindow(getLayoutInflater().inflate(R.layout.exchange_info_popup_layout, null, false), 500, 500, true);
                window.setBackgroundDrawable(null);
                window.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);
                window.showAtLocation(findViewById(R.id.nav_host_fragment), Gravity.CENTER, 0, 0);
                return true;
            case R.id.action_add_payment:
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

                if (PaymentsAddFragment.isIsEnabled()) return false;

                navHostFragment.getNavController().navigate(R.id.action_paymentsFragment_to_paymentsAddFragment);
                return true;
            case R.id.action_settings:
                navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                navHostFragment.getNavController().navigate(R.id.action_homeFragment_to_homeSettingsFragment);

        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Attempting to send data!");

        try {
            new JSONPostRequest("https://phqsh.me/update_user")
                    .post(UserDataHolder.getGson().toJson(UserDataHolder.getInstance()))
                    .thenAccept(System.out::println);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}