package com.androilk.bifs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androilk.bifs.fragment.Home;
import com.androilk.bifs.fragment.Photo;
import com.androilk.bifs.fragment.Recipe;
import com.androilk.bifs.fragment.RecipeList;
import com.androilk.bifs.fragment.Setting;
import com.androilk.bifs.fragment.ShopAdd;
import com.androilk.bifs.fragment.ShopList;
import com.androilk.bifs.fragment.StockAdd;
import com.androilk.bifs.fragment.UserProfile;
import com.androilk.bifs.fragment.fridge_fragment;
import com.androilk.bifs.login.LoginScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by FBI on 3.02.2018.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private TextView name;
    private TextView email;
    private ImageView photo;
    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Home home = new Home();
        FragmentManager fragmentManager3;
        fragmentManager3 = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager3.beginTransaction();
        fragmentTransaction.replace(R.id.container, home).addToBackStack(null).commit();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.basketadd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager3;
                fragmentManager3 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager3.beginTransaction();
                ShopAdd sh = new ShopAdd();
                fragmentTransaction.replace(R.id.container, sh);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                Toast.makeText(MainActivity.this, "Sepete Ekle", Toast.LENGTH_SHORT).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        user = mAuth.getInstance().getCurrentUser();
        name = (TextView) header.findViewById(R.id.name);
        email = (TextView) header.findViewById(R.id.email);
        photo = (ImageView) header.findViewById(R.id.photo);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference profil = mDatabase.child("bifs-users");
        DatabaseReference userProfil = profil.child(user.getUid());
        userProfil.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("user_name").getValue() !=null)
                name.setText(String.valueOf(dataSnapshot.child("user_name").getValue()));
                else
                    name.setText("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final long ONE_MEGABYTE = 1024 * 1024;
       /* ref = FirebaseStorage.getInstance().getReference(user.getUid().toString());
       
            //download file as a byte array

            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Toast.makeText(MainActivity.this, bytes.length, Toast.LENGTH_SHORT).show();
                    photo.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    photo.setVisibility(View.INVISIBLE);
                }
            });
*/
        email.setText(user.getEmail());


        Toast.makeText(this, "Hoşgeldiniz " + user.getEmail(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            FragmentManager fragmentManager;
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
            Setting setting = new Setting();
            fragmentTransaction2.replace(R.id.container, setting);
            fragmentTransaction2.addToBackStack(null);
            fragmentTransaction2.commit();
            Toast.makeText(this, "Ayarlar", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            Toast.makeText(this, "Başarıyla Çıkış Yapıldı", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();

        /**
         * Call Menu/activty_main_drawer.xml items id
         */
        if (id == R.id.alisveris_sepeti_listele) {
            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
            ShopList shopList = new ShopList();
            fragmentTransaction2.replace(R.id.container, shopList);
            fragmentTransaction2.addToBackStack(null);
            fragmentTransaction2.commit();
            Toast.makeText(this, "Alısveriş Sepeti", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.home)
        {
            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
            Home home = new Home();
            fragmentTransaction2.replace(R.id.container, home);
            fragmentTransaction2.addToBackStack(null);
            fragmentTransaction2.commit();
            Toast.makeText(this, "Anasayfa", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.user_profile)
        {
            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
            UserProfile profile = new UserProfile();
            fragmentTransaction2.replace(R.id.container, profile);
            fragmentTransaction2.addToBackStack(null);
            fragmentTransaction2.commit();

        }
        else if(id == R.id.recipe)
        {
            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
            Recipe recipe = new Recipe();
            fragmentTransaction2.replace(R.id.container, recipe);
            fragmentTransaction2.addToBackStack(null);
            fragmentTransaction2.commit();
            Toast.makeText(this, "Tarif Ekle", Toast.LENGTH_SHORT).show();

        }

        else if(id == R.id.recipeList)
        {
            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
            RecipeList recipeList = new RecipeList();
            fragmentTransaction2.replace(R.id.container, recipeList);
            fragmentTransaction2.addToBackStack(null);
            fragmentTransaction2.commit();
            Toast.makeText(this, "Tarif Listele", Toast.LENGTH_SHORT).show();

        }
        else if(id == R.id.storage)
        {
            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
            fridge_fragment storage = new fridge_fragment();
            fragmentTransaction2.replace(R.id.container, storage);
            fragmentTransaction2.addToBackStack(null);
            fragmentTransaction2.commit();
            Toast.makeText(this, "Dolabım", Toast.LENGTH_SHORT).show();


        }

        else if(id == R.id.photo)
        {
            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
            Photo photo = new Photo();
            fragmentTransaction2.replace(R.id.container, photo);
            fragmentTransaction2.addToBackStack(null);
            fragmentTransaction2.commit();
            Toast.makeText(this, "Photo", Toast.LENGTH_SHORT).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
