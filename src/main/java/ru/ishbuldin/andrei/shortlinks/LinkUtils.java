package ru.ishbuldin.andrei.shortlinks;

import org.apache.commons.validator.routines.UrlValidator;
import java.util.ArrayList;
import java.util.List;

public class LinkUtils {

    static int generateRank(LinkRepository linkRepository) {
        return (int) linkRepository.count() + 1;
    }

    static String generateShortLink() {
        char[] chars = {'0', '1', '2', '3', '4', '5'
                , '6', '7', '8', '9', 'a', 'b', 'c'
                , 'd', 'e', 'f', 'g', 'h', 'i', 'j'
                , 'k', 'l', 'm', 'n', 'o', 'p', 'q'
                , 'r', 's', 't', 'u', 'v', 'w', 'x'
                , 'y', 'z', 'A', 'B', 'C', 'D', 'E'
                , 'F', 'G', 'H', 'I', 'J', 'K', 'L'
                , 'M', 'N', 'O', 'P', 'Q', 'R', 'S'
                , 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        int max = chars.length;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(chars[random(max)]);
        }
        return stringBuilder.toString();
    }

    private static int random(int max) {
        return (int) (Math.random() * max);
    }

    static List<Link> calculateRanks(Link link, List<Link> linksWithSameCounter) {
        List<Link> calculatedLinks = new ArrayList<>();
        Link linkWithBestRank = searchBestRank(linksWithSameCounter);
        if (linksWithSameCounter.size() > 1) {
            if (linkWithBestRank.getId() != link.getId()) {
                int oldRank = link.getRank();
                link.setRank(linkWithBestRank.getRank());
                link.setCount(link.getCount() + 1);
                calculatedLinks.add(link);
                for (Link l : linksWithSameCounter) {
                    if (l.getId() != link.getId() && l.getRank() < oldRank) {
                        l.setRank(l.getRank() + 1);
                        calculatedLinks.add(l);
                    }
                }
            }
            else {
                link.setCount(link.getCount() + 1);
                calculatedLinks.add(link);
            }
        }
        else {
            link.setCount(link.getCount() + 1);
            calculatedLinks.add(link);
        }
        return calculatedLinks;
    }

    private static Link searchBestRank(List<Link> links) {
        Link result = links.get(0);
        int bestRank = links.get(0).getRank();
        for (Link link : links)
            if (link.getRank() < bestRank) result = link;
        return result;
    }

    static boolean isValidURL(String urlForValidate) {
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        return urlValidator.isValid(urlForValidate);
    }

}