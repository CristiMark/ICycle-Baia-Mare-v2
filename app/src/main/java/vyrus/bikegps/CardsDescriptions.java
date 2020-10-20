package vyrus.bikegps;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cristi Mark on 09.06.2017.
 */

public class CardsDescriptions {
    private String title;
    private String markerColor;
    private String description;
    private LatLng coordination;
    private List<String> imagePath;
    private String owner;

    public CardsDescriptions(final String title, final String markerColor, final String description, final LatLng coordination, final List<String> imagePath, final String owner) {
        this.title = title;
        this.markerColor = markerColor;
        this.description = description;
        this.coordination = coordination;
        this.imagePath = new ArrayList<>();
        this.imagePath = imagePath;
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public String getMarkerColor() {
        return markerColor;
    }

    public String getDescription() {
        return description;
    }

    public LatLng getCoordination() {
        return coordination;
    }

    public List<String> getImagePath() {
        return imagePath;
    }

    public String getOwner() {
        return owner;
    }
}
