package ru.ishbuldin.andrei.shortlinks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    LinkRepository linkRepository;

    @InjectMocks
    WebController webController;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(webController)
                .build();
    }


    @Test
    public void addLink_ShouldAddLinkAndReturnAddedLink() throws Exception {

        OriginalLinkForm originalLinkForm = new OriginalLinkForm("http://test.ru");

        mockMvc.perform(post("/generate")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(originalLinkForm)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.link", notNullValue()));
    }

    @Test
    public void addLink_ShouldReturn404Error() throws Exception {

        OriginalLinkForm originalLinkForm = new OriginalLinkForm("");

        mockMvc.perform(post("/generate")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(originalLinkForm)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void one_ShouldReturnRedirectToOriginal() throws Exception {

        Link link = new Link(0, "aBcDe1", "http://kontur.ru", 1, 0);
        List<Link> links = new ArrayList<>();
        links.add(link);

        when(linkRepository.findByLink(link.getLink())).thenReturn(link);
        when(linkRepository.findByCount(link.getCount())).thenReturn(links);
        mockMvc.perform(get("/l/{url}", link.getLink()))
                //.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(link.getOriginal()));
        verify(linkRepository, times(1)).findByLink(link.getLink());
    }

    @Test
    public void one_ShouldReturn404Error() throws Exception {
        when(linkRepository.findByLink("any")).thenReturn(null);
        mockMvc.perform(get("/l/{url}", "any"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void allLinksStat_ShouldReturnStatForLinkWithId2FromPage2WithPageSize1() throws Exception {
        final int PAGE_NUMBER = 2;
        final int COUNT_OF_ELEMENTS_ON_PAGE = 1;
        Link link = new Link(0, "aBcDe1", "http://kontur.ru", 1, 0);
        Link link1 = new Link(1, "asdfZ2", "http://yandex.ru", 2, 0);
        Link link2 = new Link(2, "zxcv12", "http://mail.ru", 3, 0);
        List<Link> links = new ArrayList<>();
        links.add(link);
        links.add(link1);
        links.add(link2);
        Pageable pageable = PageRequest.of(PAGE_NUMBER, COUNT_OF_ELEMENTS_ON_PAGE, Sort.by("rank").ascending());
        //Page<Link> page = new PageImpl<>(links, pageable, links.size());
        PagedListHolder pagedListHolder = new PagedListHolder(links);
        pagedListHolder.setPageSize(COUNT_OF_ELEMENTS_ON_PAGE);
        pagedListHolder.setPage(PAGE_NUMBER);
        Page<Link> page = new PageImpl<>(pagedListHolder.getPageList());

        when(linkRepository.findAll(pageable)).thenReturn(page);
        mockMvc.perform(get("/stats")
                .param("page", String.valueOf(PAGE_NUMBER))
                .param("count", String.valueOf(COUNT_OF_ELEMENTS_ON_PAGE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].link", is("zxcv12")))
                .andExpect(jsonPath("$[0].original", is("http://mail.ru")));
        verify(linkRepository, times(1)).findAll(pageable);
    }

    @Test
    public void oneLinkStat_ShouldReturnStatForLinkFromUrl() throws Exception {
        Link link = new Link(0, "aBcDe1", "http://kontur.ru", 1, 0);

        when(linkRepository.findByLink(link.getLink())).thenReturn(link);
        mockMvc.perform(get("/stats/{url}", link.getLink()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.original", is(link.getOriginal())));
        verify(linkRepository, times(1)).findByLink(link.getLink());
    }

    @Test
    public void oneLinkStat_ShouldReturn404Error() throws Exception {
        when(linkRepository.findByLink("isNotExist")).thenReturn(null);
        mockMvc.perform(get("/stats/{url}", "isNotExist"))
                .andExpect(status().isNotFound());
    }

}