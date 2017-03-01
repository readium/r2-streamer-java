package com.codetoart.r2_streamer.model.publication.metadata;

import com.codetoart.r2_streamer.model.publication.subject.Subject;
import com.codetoart.r2_streamer.model.publication.contributor.Contributor;
import com.codetoart.r2_streamer.model.publication.rendition.Rendition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class MetaData {
    public String title;
    public String identifier;

    public List<Contributor> creators;
    //public Contributor[] authors;
    public List<Contributor> translators;
    //public Contributor[] translators;
    public List<Contributor> editors;
    //public Contributor[] editors;
    public List<Contributor> artists;
    //public Contributor[] artists;
    public List<Contributor> illustrators;
    //public Contributor[] illustrators;
    public List<Contributor> letterers;
    //public Contributor[] letterers;
    public List<Contributor> pencilers;
    //public Contributor[] pencilers;
    public List<Contributor> colorists;
    //public Contributor[] colorists;
    public List<Contributor> inkers;
    //public Contributor[] inkers;
    public List<Contributor> narrators;
    //public Contributor[] narrators;
    public List<Contributor> contributors;
    //public Contributor[] contributors;
    public List<Contributor> publishers;
    //public Contributor[] publishers;
    public List<Contributor> imprints;
    //public Contributor[] imprints;

    public List<String> languages;
    public Date modified;
    public Date publicationDate;
    public String description;
    public String direction;
    public Rendition rendition;
    public String source;
    public String[] epubType;
    public List<String> rights;
    public List<Subject> subjects;
    //public Subject[] subjects;

    //private List<MetadataItem> otherMetadata;
    public MetadataItem[] otherMetadata;

    public MetaData() {
        this.rendition = new Rendition();
        this.creators = new ArrayList<Contributor>();
        this.translators = new ArrayList<Contributor>();
        this.editors = new ArrayList<Contributor>();
        this.artists = new ArrayList<Contributor>();
        this.illustrators = new ArrayList<Contributor>();
        this.letterers = new ArrayList<Contributor>();
        this.pencilers = new ArrayList<Contributor>();
        this.colorists = new ArrayList<Contributor>();
        this.inkers = new ArrayList<Contributor>();
        this.narrators = new ArrayList<Contributor>();
        this.contributors = new ArrayList<Contributor>();
        this.publishers = new ArrayList<Contributor>();
        this.imprints = new ArrayList<Contributor>();
        this.languages = new ArrayList<String>();
        this.rights = new ArrayList<String>();
        this.subjects = new ArrayList<Subject>();
    }

    public MetaData(String title, String identifier, List<Contributor> creators, List<Contributor> translators, List<Contributor> editors, List<Contributor> artists, List<Contributor> illustrators, List<Contributor> letterers, List<Contributor> pencilers, List<Contributor> colorists, List<Contributor> inkers, List<Contributor> narrators, List<Contributor> contributors, List<Contributor> publishers, List<Contributor> imprints, List<String> languages, Date modified, Date publicationDate, String description, String direction, Rendition rendition, String source, String[] epubType, List<String> rights, List<Subject> subjects, MetadataItem[] otherMetadata) {
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

    public String[] getEpubType() {
        return epubType;
    }

    public void setEpubType(String[] epubType) {
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

    public MetadataItem[] getOtherMetadata() {
        return otherMetadata;
    }

    public void setOtherMetadata(MetadataItem[] otherMetadata) {
        this.otherMetadata = otherMetadata;
    }
}