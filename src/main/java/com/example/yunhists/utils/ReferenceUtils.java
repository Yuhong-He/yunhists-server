package com.example.yunhists.utils;

public class ReferenceUtils {

    public static String vancouverStyle(String author, String title, String publication, String location,
                                        String publisher, String year, String volume, String issue,
                                        String pages, String doi, int type) {
        StringBuilder vancouver = new StringBuilder();
        if(!author.isEmpty()) {
            vancouver.append(author);
            vancouver.append(". ");
        }
        vancouver.append(title);
        if(type == 1 || type == 2) {
            if(!publication.isEmpty()) {
                vancouver.append(". In: ");
                vancouver.append(publication);
                vancouver.append(". ");
            }
            if(!location.isEmpty()) {
                vancouver.append(location);
                vancouver.append(": ");
            }
            if(!publisher.isEmpty()) {
                vancouver.append(publisher);
                vancouver.append("; ");
            }
            if(!year.isEmpty()) {
                vancouver.append(year);
            }
        } else { // type = 0
            if(!publication.isEmpty()) {
                vancouver.append(". ");
                vancouver.append(publication);
            }
            if(!year.isEmpty()) {
                vancouver.append(". ");
                vancouver.append(year);
                vancouver.append("; ");
            }
            if(!volume.isEmpty()) {
                vancouver.append(volume);
            }
            if(!issue.isEmpty()) {
                vancouver.append("(");
                vancouver.append(issue);
                vancouver.append(")");
            }
            if(!pages.isEmpty()) {
                vancouver.append(": ");
                vancouver.append(pages);
            }
        }
        if(!doi.isEmpty()) {
            vancouver.append(". Available from: ");
            vancouver.append(doi);
        }
        vancouver.append(".");

        return String.valueOf(vancouver);
    }

    public static String harvardStyle(String author, String title, String publication, String location,
                                      String publisher, String year, String volume, String issue,
                                      String pages, String doi, int type) {
        StringBuilder harvard = new StringBuilder();
        if(!author.isEmpty()) {
            harvard.append(author);
        }
        if(!year.isEmpty()) {
            harvard.append(" (");
            harvard.append(year);
            harvard.append("). ");
        }
        harvard.append("'");
        harvard.append(title);
        harvard.append("'");
        if(type == 1 || type == 2) {
            if(!publication.isEmpty()) {
                harvard.append(" in ");
                harvard.append(publication);
                harvard.append(". ");
            }
            if(!location.isEmpty()) {
                harvard.append(location);
                harvard.append(": ");
            }
            if(!publisher.isEmpty()) {
                harvard.append(publisher);
                harvard.append(", ");
            }
        } else { // type = 0
            if(!publication.isEmpty()) {
                harvard.append(". ");
                harvard.append(publication);
            }
            if(!volume.isEmpty()) {
                harvard.append(", ");
                harvard.append(volume);
            }
            if(!issue.isEmpty()) {
                harvard.append("(");
                harvard.append(issue);
                harvard.append("), ");
            }
        }
        if(!pages.isEmpty()) {
            harvard.append("pp.");
            harvard.append(pages);
        }
        if(!doi.isEmpty()) {
            harvard.append(". doi:");
            harvard.append(doi);
        }
        harvard.append(".");

        return String.valueOf(harvard);
    }

    public static String gbt7714Style(String author, String title, String publication, String location,
                                      String publisher, String year, String volume, String issue,
                                      String pages, int type) {
        StringBuilder gbt7714 = new StringBuilder();
        if(!author.isEmpty()) {
            author = author.replaceAll(",", "，");
            gbt7714.append(author);
            gbt7714.append("．");
        }
        gbt7714.append(title);
        if(type == 0) {
            gbt7714.append("[J]");
        } else if(type == 1) {
            gbt7714.append("[G]");
        } else if(type == 2) {
            gbt7714.append("[M]");
        }
        if(type == 1 || type == 2) {
            if(!publication.isEmpty()) {
                gbt7714.append("．");
                gbt7714.append(publication);
                gbt7714.append("．");
            }
            if(!location.isEmpty()) {
                gbt7714.append(location);
                gbt7714.append("：");
            }
            if(!publisher.isEmpty()) {
                gbt7714.append(publisher);
                gbt7714.append("，");
            }
            if(!year.isEmpty()) {
                gbt7714.append(year);
            }
        } else { // type = 0
            if(!publication.isEmpty()) {
                gbt7714.append("．");
                gbt7714.append(publication);
            }
            if(!year.isEmpty()) {
                gbt7714.append("，");
                gbt7714.append(year);
                gbt7714.append("，");
            }
            if(!volume.isEmpty()) {
                gbt7714.append(volume);
            }
            if(!issue.isEmpty()) {
                gbt7714.append("(");
                gbt7714.append(issue);
                gbt7714.append(")");
            }
        }
        if(!pages.isEmpty()) {
            gbt7714.append("：");
            gbt7714.append(pages);
        }
        gbt7714.append("．");

        return String.valueOf(gbt7714);
    }

    public static String wikipediaStyle(String author, String title, String publication, String location,
                                        String publisher, String year, String volume, String issue,
                                        String pages, String isbn, String doi, int type) {
        StringBuilder wikipedia = new StringBuilder();
        if(type == 0) {
            wikipedia.append("{{cite journal");
        } else {
            wikipedia.append("{{cite book");
        }
        if(!author.isEmpty()) {
            if(author.contains(",")) {
                int i = 0;
                for (String au: author.split(",")){
                    i++;
                    wikipedia.append(" |author");
                    wikipedia.append(i);
                    wikipedia.append("=");
                    wikipedia.append(au);
                }
            } else {
                wikipedia.append(" |author=");
                wikipedia.append(author);
            }
        }
        if(type == 1 || type == 2) {
            wikipedia.append(" |chapter=");
            wikipedia.append(title);
            if(!publication.isEmpty()) {
                wikipedia.append(" |title=");
                wikipedia.append(publication);
            }
            if(!location.isEmpty()) {
                wikipedia.append(" |location=");
                wikipedia.append(location);
            }
            if(!publisher.isEmpty()) {
                wikipedia.append(" |publisher=");
                wikipedia.append(publisher);
            }
            if(!year.isEmpty()) {
                wikipedia.append(" |year=");
                wikipedia.append(year);
            }
            if(!isbn.isEmpty()) {
                wikipedia.append(" |isbn=");
                wikipedia.append(isbn);
            }
        } else { // type = 0
            wikipedia.append(" |title=");
            wikipedia.append(title);
            if(!publication.isEmpty()) {
                wikipedia.append(" |journal=");
                wikipedia.append(publication);
            }
            if(!year.isEmpty()) {
                wikipedia.append(" |year=");
                wikipedia.append(year);
            }
            if(!volume.isEmpty()) {
                wikipedia.append(" |volume=");
                wikipedia.append(volume);
            }
            if(!issue.isEmpty()) {
                wikipedia.append(" |issue=");
                wikipedia.append(issue);
            }
        }
        if(!pages.isEmpty()) {
            wikipedia.append(" |pages=");
            wikipedia.append(pages);
        }
        if(!doi.isEmpty()) {
            wikipedia.append(" |doi=");
            wikipedia.append(doi);
        }
        wikipedia.append(" }}");

        return String.valueOf(wikipedia);
    }

}
