package ru.ishbuldin.andrei.shortlinks;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class LinkUtilsTest {

    private Link link;
    private Link link1;
    private Link link2;
    private Link link3;
    private List<Link> links;

    @Before
    public void setUp() {
        link = new Link(0, "aBcDe1", "http://kontur.ru", 1, 0);
        link1 = new Link(1, "asdfZ2", "http://yandex.ru", 2, 0);
        link2 = new Link(2, "zxcv12", "http://mail.ru", 3, 0);
        link3 = new Link(3, "Dg56s2", "http://google.com", 4, 0);
        links = new ArrayList<>();
        links.add(link);
        links.add(link1);
        links.add(link2);
        links.add(link3);
    }

    @Test
    public void calculateRanks_ShouldCount1Rank1OnLink() {
        List<Link> expected = LinkUtils.calculateRanks(link, links);

        Link changedLink = links.get(0);
        changedLink.setCount(1);
        List<Link> actual = new ArrayList<>();
        actual.add(changedLink);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void calculateRanks_ShouldCount1Rank1OnLink1AndRank2OnLink() {
        List<Link> expected = LinkUtils.calculateRanks(link1, links);

        Link changedLink = links.get(0);
        Link changedLink1 = links.get(1);
        changedLink.setRank(2);
        changedLink1.setRank(1);
        changedLink1.setCount(1);
        List<Link> actual = new ArrayList<>();
        actual.add(changedLink1);
        actual.add(changedLink);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void calculateRanks_ShouldCount1Rank1OnLink2_AndRank2OnLink_AndRank3OnLink1() {
        List<Link> expected = LinkUtils.calculateRanks(link2, links);

        Link changedLink = links.get(0);
        Link changedLink1 = links.get(1);
        Link changedLink2 = links.get(2);
        changedLink.setRank(2);
        changedLink1.setRank(3);
        changedLink2.setRank(1);
        changedLink2.setCount(1);
        List<Link> actual = new ArrayList<>();
        actual.add(changedLink2);
        actual.add(changedLink);
        actual.add(changedLink1);

        Assert.assertEquals(expected, actual);
    }

}