package org.readium.r2_streamer.model.publication.metadata;

import org.readium.r2_streamer.model.publication.subject.Subject;
import org.readium.r2_streamer.model.publication.contributor.Contributor;
import org.readium.r2_streamer.model.publication.rendition.Rendition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class MetaData implements Serializable {
    private static final long serialVersionUID = 8526472295622776148L;
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
        this.otherMetadata = new ArrayList<>();
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

    @Override
    public String toString() {
        return "MetaData{" +
                "title='" + title + '\'' +
                ", identifier='" + identifier + '\'' +
                ", creators=" + creators +
                ", translators=" + translators +
                ", editors=" + editors +
                ", artists=" + artists +
                ", illustrators=" + illustrators +
                ", letterers=" + letterers +
                ", pencilers=" + pencilers +
                ", colorists=" + colorists +
                ", inkers=" + inkers +
                ", narrators=" + narrators +
                ", contributors=" + contributors +
                ", publishers=" + publishers +
                ", imprints=" + imprints +
                ", languages=" + languages +
                ", modified=" + modified +
                ", publicationDate=" + publicationDate +
                ", description='" + description + '\'' +
                ", direction='" + direction + '\'' +
                ", rendition=" + rendition +
                ", source='" + source + '\'' +
                ", epubType=" + epubType +
                ", rights=" + rights +
                ", subjects=" + subjects +
                ", otherMetadata=" + otherMetadata +
                '}';
    }

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
}