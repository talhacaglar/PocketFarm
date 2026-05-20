package com.tarlamcebimde.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.Product;
import com.tarlamcebimde.app.repository.ProductRepository;
import com.tarlamcebimde.app.ui.product.ProductDetailActivity;
import com.tarlamcebimde.app.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Harita fragment'ı - Ürün konumlarını Google Maps üzerinde gösterir
 * Marker'a tıklandığında ürün detay sayfası açılır
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap googleMap;
    private ProductRepository productRepository;
    private Map<String, Product> markerProductMap; // marker id -> product

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productRepository = new ProductRepository();
        markerProductMap = new HashMap<>();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;

        // Türkiye merkezine odakla
        LatLng turkey = new LatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkey, Constants.DEFAULT_ZOOM));

        // Info window tıklama dinleyicisi
        googleMap.setOnInfoWindowClickListener(this);

        // Ürünleri yükle ve marker olarak göster
        loadProductMarkers();
    }

    private void loadProductMarkers() {
        productRepository.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            if (products == null || googleMap == null) return;

            googleMap.clear();
            markerProductMap.clear();

            for (Product product : products) {
                double lat = product.getLatitude();
                double lng = product.getLongitude();

                if (lat != 0 && lng != 0) {
                    LatLng position = new LatLng(lat, lng);
                    String snippet = String.format("₺%.2f/kg - %s\nDetay için tıklayın",
                            product.getPricePerKg(), product.getCategory());

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(product.getTitle())
                            .snippet(snippet));

                    if (marker != null) {
                        markerProductMap.put(marker.getId(), product);
                    }
                }
            }
        });
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Product product = markerProductMap.get(marker.getId());
        if (product != null) {
            // Ürün detay sayfasını aç - veritabanından bilgi gösterilir
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.getProductId());
            startActivity(intent);
        }
    }
}
