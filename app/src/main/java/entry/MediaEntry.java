package entry;


/**
 * Created by HuongLX on 3/12/2017.
 */

public class MediaEntry {
    private int id;
    private String path;
    private String title;
    private Long dateAdded;
    private String latitude;
    private String longtitude;
    private String description;
    private String tag;
    private String album;
    private String artist;
    private int type;

    public MediaEntry(int id, String path, String title, Long dateAdded, String latitude, String longtitude, String description, String tag, String album, String artist, int type) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.dateAdded = dateAdded;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.description = description;
        this.tag = tag;
        this.album = album;
        this.artist = artist;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}