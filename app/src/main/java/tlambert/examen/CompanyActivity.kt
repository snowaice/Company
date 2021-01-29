package tlambert.examen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_company.*
import tlambert.examen.model.Company

class CompanyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company)

        //Affichage des info entreprise
        var company = intent.getSerializableExtra("company") as Company
        TV_Libelle.text = company.libelle
        TV_siret.text = "Siret : "+company.siret
        TV_acti.text = "Activité : "+company.activite
        TV_acti1.text = "code NAF : "+company.activite_principale
        TV_region.text = "Région : "+company.region
        TV_cp.text = "Code Postal : "+company.cp

        //affichage map
        var mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            var googleMap = it
            val loc = LatLng(company.latitude!!.toDouble(), company.longitude!!.toDouble())
            googleMap.addMarker(MarkerOptions().position(loc).title(company.libelle).visible(true))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,14f))
        })

    }
}