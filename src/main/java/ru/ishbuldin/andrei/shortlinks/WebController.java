package ru.ishbuldin.andrei.shortlinks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WebController {

    @Autowired
    private LinkRepository linkRepository;

    //POST LINK
    //example: curl -X POST host/generate -H 'Content-type:application/json' -d '{"original":"http:://rambler.ru"}'
    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public Map<String, String> addLink(@RequestBody OriginalLinkForm originalLinkForm) throws Exception {
        String originalUrl = originalLinkForm.getOriginal();
        int rank = LinkUtils.generateRank(linkRepository);
        String shortLink = null;
        if (LinkUtils.isValidURL(originalUrl)) {
            HashMap<String, String> resultMap = new HashMap<>();
            if (linkRepository.existsByOriginal(originalUrl)) {
                resultMap.put("link", linkRepository.findByOriginal(originalUrl).getLink());
                return resultMap;
            }
            else {
                shortLink = LinkUtils.generateShortLink();
                while (linkRepository.existsByLink(shortLink))
                {
                    shortLink = LinkUtils.generateShortLink();
                }
                Link link = new Link(originalUrl, shortLink, rank);
                linkRepository.save(link);
                resultMap.put("link", link.getLink());
                return resultMap;
            }
        }
        else throw new ResourceNotFoundException("URL is not valid");
    }

    //GET ONE
    @RequestMapping(value = "/l/{url}", method = RequestMethod.GET)
    public void one(@PathVariable String url, HttpServletResponse response) throws IOException {
        Link link = linkRepository.findByLink(url);
        if (link != null) {
            List<Link> links = linkRepository.findByCount(link.getCount());
            links = LinkUtils.calculateRanks(link, links);
            linkRepository.saveAll(links);
            response.sendRedirect(link.getOriginal());
        }
        else throw new ResourceNotFoundException("This is link not found");
    }

    /*
    GET ALL STAT WITH PAGINATION
    Path Variables @page and @size
    Pages start from 0, example: curl -X GET -G host/stats -d 'page=0' -d 'size=1'
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public List<Link> allLinksStat(@PageableDefault(sort = {"rank"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return linkRepository.findAll(pageable).getContent();
    }
    */

    //GET ALL STAT WITH PAGINATION
    //Path Variables @page and @count
    //Pages start from 0, example: curl -X GET -G host/stats -d 'page=0' -d 'count=1'
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public List<Link> allLinkStat(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "count", defaultValue = "100") int count) {
        count = count > 100 ? 100 : count;
        Pageable pageable = PageRequest.of(page, count, Sort.by("rank").ascending());
        List<Link> links = linkRepository.findAll(pageable).getContent();
        if (!links.isEmpty()) {
            return links;
        }
        else {
            throw new ResourceNotFoundException();
        }
    }

    //GET ONE STAT
    @RequestMapping(value = "stats/{url}", method = RequestMethod.GET)
    public Link oneLinkStat(@PathVariable String url) {
        Link link = linkRepository.findByLink(url);
        if (link != null) {
            return link;
        }
        else {
            throw new ResourceNotFoundException();
        }
    }

}