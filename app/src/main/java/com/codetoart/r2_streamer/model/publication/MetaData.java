package com.codetoart.r2_streamer.model.publication;

import com.codetoart.r2_streamer.model.publication.rendition.Rendition;

import java.util.Date;

/**
 * Created by Shrikant on 25-Jan-17.
 */

public class MetaData {
    private String title;
    private String identifier;

    //private List<Contributor> authors;
    private Contributor[] authors;
    //private List<Contributor> translators;
    private Contributor[] translators;
    //private List<Contributor> editors;
    private Contributor[] editors;
    //private List<Contributor> artists;
    private Contributor[] artists;
    //private List<Contributor> illustrators;
    private Contributor[] illustrators;
    //private List<Contributor> letterers;
    private Contributor[] letterers;
    //private List<Contributor> pencilers;
    private Contributor[] pencilers;
    //private List<Contributor> colorists;
    private Contributor[] colorists;
    //private List<Contributor> inkers;
    private Contributor[] inkers;
    //private List<Contributor> narrators;
    private Contributor[] narrators;
    //private List<Contributor> contributors;
    private Contributor[] contributors;
    //private List<Contributor> publishers;
    private Contributor[] publishers;
    //private List<Contributor> imprints;
    private Contributor[] imprints;

    private String languages;
    private Date modified;
    private Date publicationDate;
    private String description;
    private String direction;
    private Rendition rendition;
    private String source;
    private String[] epubType;
    private String rights;
    //private List<Subject> subjects;
    private Subject[] subjects;

    //private List<MetadataItem> otherMetadata;
    private MetadataItem[] otherMetadata;

    public MetaData() {
    }

    public MetaData(String title, String identifier, Contributor[] authors, Contributor[] translators, Contributor[] editors, Contributor[] artists, Contributor[] illustrators, Contributor[] letterers, Contributor[] pencilers, Contributor[] colorists, Contributor[] inkers, Contributor[] narrators, Contributor[] contributors, Contributor[] publishers, Contributor[] imprints, String languages, Date modified, Date publicationDate, String description, String direction, Rendition rendition, String source, String[] epubType, String rights, Subject[] subjects, MetadataItem[] otherMetadata) {
        this.title = title;
        this.identifier = identifier;
        this.authors = authors;
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
        this.direction = direction;     // = "default";
        this.rendition = rendition;
        this.source = source;
        this.epubType = epubType;
        this.rights = rights;
        this.subjects = subjects;
        this.otherMetadata = otherMetadata;
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

    public Contributor[] getAuthors() {
        return authors;
    }

    public void setAuthors(Contributor[] authors) {
        this.authors = authors;
    }

    public Contributor[] getTranslators() {
        return translators;
    }

    public void setTranslators(Contributor[] translators) {
        this.translators = translators;
    }

    public Contributor[] getEditors() {
        return editors;
    }

    public void setEditors(Contributor[] editors) {
        this.editors = editors;
    }

    public Contributor[] getArtists() {
        return artists;
    }

    public void setArtists(Contributor[] artists) {
        this.artists = artists;
    }

    public Contributor[] getIllustrators() {
        return illustrators;
    }

    public void setIllustrators(Contributor[] illustrators) {
        this.illustrators = illustrators;
    }

    public Contributor[] getLetterers() {
        return letterers;
    }

    public void setLetterers(Contributor[] letterers) {
        this.letterers = letterers;
    }

    public Contributor[] getPencilers() {
        return pencilers;
    }

    public void setPencilers(Contributor[] pencilers) {
        this.pencilers = pencilers;
    }

    public Contributor[] getColorists() {
        return colorists;
    }

    public void setColorists(Contributor[] colorists) {
        this.colorists = colorists;
    }

    public Contributor[] getInkers() {
        return inkers;
    }

    public void setInkers(Contributor[] inkers) {
        this.inkers = inkers;
    }

    public Contributor[] getNarrators() {
        return narrators;
    }

    public void setNarrators(Contributor[] narrators) {
        this.narrators = narrators;
    }

    public Contributor[] getContributors() {
        return contributors;
    }

    public void setContributors(Contributor[] contributors) {
        this.contributors = contributors;
    }

    public Contributor[] getPublishers() {
        return publishers;
    }

    public void setPublishers(Contributor[] publishers) {
        this.publishers = publishers;
    }

    public Contributor[] getImprints() {
        return imprints;
    }

    public void setImprints(Contributor[] imprints) {
        this.imprints = imprints;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
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

    public String[] getEpubType() {
        return epubType;
    }

    public void setEpubType(String[] epubType) {
        this.epubType = epubType;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public Subject[] getSubjects() {
        return subjects;
    }

    public void setSubjects(Subject[] subjects) {
        this.subjects = subjects;
    }

    public MetadataItem[] getOtherMetadata() {
        return otherMetadata;
    }

    public void setOtherMetadata(MetadataItem[] otherMetadata) {
        this.otherMetadata = otherMetadata;
    }
}