package com.readium.r2_streamer.model.publication.metadata;

import android.os.Parcel;
import android.os.Parcelable;

import com.readium.r2_streamer.model.publication.subject.Subject;
import com.readium.r2_streamer.model.publication.contributor.Contributor;
import com.readium.r2_streamer.model.publication.rendition.Rendition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class MetaData implements Parcelable{
    public String title;
    public String identifier;

    public List<Contributor> creators;
    public List<Contributor> translators;
    public List<Contributor> editors;
    public List<Contributor> artists;
    public List<Contributor> illustrators;
    public List<Contributor> letterers;
    public List<Contributor> pencilers;
    public List<Contributor> colorists;
    public List<Contributor> inkers;
    public List<Contributor> narrators;
    public List<Contributor> contributors;
    public List<Contributor> publishers;
    public List<Contributor> imprints;

    public List<String> languages;
    public Date modified;
    public Date publicationDate;
    public String description;
    public String direction;
    public Rendition rendition;
    public String source;
    public List<String> epubType;
    public List<String> rights;
    public List<Subject> subjects;

    private List<MetadataItem> otherMetadata;

    public MetaData() {
        this.rendition = new Rendition();
        this.creators = new ArrayList<>();
        this.translators = new ArrayList<>();
        this.editors = new ArrayList<>();
        this.artists = new ArrayList<>();
        this.illustrators = new ArrayList<>();
        this.letterers = new ArrayList<>();
        this.pencilers = new ArrayList<>();
        this.colorists = new ArrayList<>();
        this.inkers = new ArrayList<>();
        this.narrators = new ArrayList<>();
        this.contributors = new ArrayList<>();
        this.publishers = new ArrayList<>();
        this.imprints = new ArrayList<>();
        this.languages = new ArrayList<>();
        this.epubType = new ArrayList<>();
        this.rights = new ArrayList<>();
        this.subjects = new ArrayList<>();
    }

    public MetaData(String title, String identifier, List<Contributor> creators, List<Contributor> translators, List<Contributor> editors, List<Contributor> artists, List<Contributor> illustrators, List<Contributor> letterers, List<Contributor> pencilers, List<Contributor> colorists, List<Contributor> inkers, List<Contributor> narrators, List<Contributor> contributors, List<Contributor> publishers, List<Contributor> imprints, List<String> languages, Date modified, Date publicationDate, String description, String direction, Rendition rendition, String source, List<String> epubType, List<String> rights, List<Subject> subjects, List<MetadataItem> otherMetadata) {
        this.title = title;
        this.identifier = identifier;
        this.creators = creators;
        this.translators = translators;
        this.editors = editors;
        this.artists = artists;
        this.illustrators = illustrators;
        this.letterers = letterers;
        this.pencilers = pencilers;
        this.colorists = colorists;
        this.inkers = inkers;
        this.narrators = narrators;
        this.contributors = contributors;
        this.publishers = publishers;
        this.imprints = imprints;
        this.languages = languages;
        this.modified = modified;
        this.publicationDate = publicationDate;
        this.description = description;
        this.direction = "default";     // = direction;
        this.rendition = rendition;
        this.source = source;
        this.epubType = epubType;
        this.rights = rights;
        this.subjects = subjects;
        this.otherMetadata = otherMetadata;
    }

    protected MetaData(Parcel in) {
        title = in.readString();
        identifier = in.readString();
        creators = in.createTypedArrayList(Contributor.CREATOR);
        translators = in.createTypedArrayList(Contributor.CREATOR);
        editors = in.createTypedArrayList(Contributor.CREATOR);
        artists = in.createTypedArrayList(Contributor.CREATOR);
        illustrators = in.createTypedArrayList(Contributor.CREATOR);
        letterers = in.createTypedArrayList(Contributor.CREATOR);
        pencilers = in.createTypedArrayList(Contributor.CREATOR);
        colorists = in.createTypedArrayList(Contributor.CREATOR);
        inkers = in.createTypedArrayList(Contributor.CREATOR);
        narrators = in.createTypedArrayList(Contributor.CREATOR);
        contributors = in.createTypedArrayList(Contributor.CREATOR);
        publishers = in.createTypedArrayList(Contributor.CREATOR);
        imprints = in.createTypedArrayList(Contributor.CREATOR);
        languages = in.createStringArrayList();
        modified = (Date) in.readSerializable();
        publicationDate = (Date) in.readSerializable();
        description = in.readString();
        direction = in.readString();
        rendition = in.readParcelable(Rendition.class.getClassLoader());
        source = in.readString();
        epubType = in.createStringArrayList();
        rights = in.createStringArrayList();
        subjects = in.createTypedArrayList(Subject.CREATOR);
        otherMetadata = in.createTypedArrayList(MetadataItem.CREATOR);
    }

    public static final Creator<MetaData> CREATOR = new Creator<MetaData>() {
        @Override
        public MetaData createFromParcel(Parcel in) {
            return new MetaData(in);
        }

        @Override
        public MetaData[] newArray(int size) {
            return new MetaData[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<Contributor> getCreators() {
        return creators;
    }

    public void setCreators(List<Contributor> creators) {
        this.creators = creators;
    }

    public List<Contributor> getTranslators() {
        return translators;
    }

    public void setTranslators(List<Contributor> translators) {
        this.translators = translators;
    }

    public List<Contributor> getEditors() {
        return editors;
    }

    public void setEditors(List<Contributor> editors) {
        this.editors = editors;
    }

    public List<Contributor> getArtists() {
        return artists;
    }

    public void setArtists(List<Contributor> artists) {
        this.artists = artists;
    }

    public List<Contributor> getIllustrators() {
        return illustrators;
    }

    public void setIllustrators(List<Contributor> illustrators) {
        this.illustrators = illustrators;
    }

    public List<Contributor> getLetterers() {
        return letterers;
    }

    public void setLetterers(List<Contributor> letterers) {
        this.letterers = letterers;
    }

    public List<Contributor> getPencilers() {
        return pencilers;
    }

    public void setPencilers(List<Contributor> pencilers) {
        this.pencilers = pencilers;
    }

    public List<Contributor> getColorists() {
        return colorists;
    }

    public void setColorists(List<Contributor> colorists) {
        this.colorists = colorists;
    }

    public List<Contributor> getInkers() {
        return inkers;
    }

    public void setInkers(List<Contributor> inkers) {
        this.inkers = inkers;
    }

    public List<Contributor> getNarrators() {
        return narrators;
    }

    public void setNarrators(List<Contributor> narrators) {
        this.narrators = narrators;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public List<Contributor> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<Contributor> publishers) {
        this.publishers = publishers;
    }

    public List<Contributor> getImprints() {
        return imprints;
    }

    public void setImprints(List<Contributor> imprints) {
        this.imprints = imprints;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Rendition getRendition() {
        return rendition;
    }

    public void setRendition(Rendition rendition) {
        this.rendition = rendition;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getEpubType() {
        return epubType;
    }

    public void setEpubType(List<String> epubType) {
        this.epubType = epubType;
    }

    public List<String> getRights() {
        return rights;
    }

    public void setRights(List<String> rights) {
        this.rights = rights;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<MetadataItem> getOtherMetadata() {
        return otherMetadata;
    }

    public void setOtherMetadata(List<MetadataItem> otherMetadata) {
        this.otherMetadata = otherMetadata;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(identifier);
        parcel.writeTypedList(creators);
        parcel.writeTypedList(translators);
        parcel.writeTypedList(editors);
        parcel.writeTypedList(artists);
        parcel.writeTypedList(illustrators);
        parcel.writeTypedList(letterers);
        parcel.writeTypedList(pencilers);
        parcel.writeTypedList(colorists);
        parcel.writeTypedList(inkers);
        parcel.writeTypedList(narrators);
        parcel.writeTypedList(contributors);
        parcel.writeTypedList(publishers);
        parcel.writeTypedList(imprints);
        parcel.writeStringList(languages);
        parcel.writeSerializable(modified);
        parcel.writeSerializable(publicationDate);
        parcel.writeString(description);
        parcel.writeString(direction);
        parcel.writeParcelable(rendition, i);
        parcel.writeString(source);
        parcel.writeStringList(epubType);
        parcel.writeStringList(rights);
        parcel.writeTypedList(subjects);
        parcel.writeTypedList(otherMetadata);
    }
}