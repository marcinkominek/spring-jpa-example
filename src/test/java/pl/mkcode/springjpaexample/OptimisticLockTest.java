package pl.mkcode.springjpaexample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import pl.mkcode.springjpaexample.model.Author;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OptimisticLockTest {
    private static final Long AUTHOR_ID = 1L;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test(expected = ExecutionException.class)
    public void testSimultaneousChange() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> setNameFuture;
        try {
            setNameFuture = executorService.submit(() -> setNameAndWait("Mike", 5));
            executorService.submit(() -> setNameAndWait("Brian", 0));
        } finally {
            executorService.awaitTermination(20, TimeUnit.SECONDS);
            executorService.shutdownNow();
        }

        setNameFuture.get();
    }

    @Test
    public void testSequentialChange() {
        setNameAndWait("Mike", 0);
        setNameAndWait("Brian", 0);
    }

    @Test
    public void testUpdate() {
        List<Author> authorContainer = new ArrayList<>();
        execInTransaction( ()-> authorContainer.add(entityManager.find(Author.class, AUTHOR_ID, LockModeType.OPTIMISTIC)));

        execInTransaction(() -> {
            Author a = authorContainer.get(0);
            Author author2 = new Author();
            author2.setId(1L);
            author2.setFirstName("Tom");
            author2.setLastName("Bing");
            author2.setUpdatedAt(a.getUpdatedAt());
            author2 = entityManager.merge(author2);
            entityManager.persist(author2);
        });

        execInTransaction(() -> {
            Author ax = entityManager.find(Author.class, AUTHOR_ID, LockModeType.OPTIMISTIC);
            System.out.println(ax);
        });
    }

    private void setNameAndWait(String name, int timeToWaitInSeconds) {
        execInTransaction(() -> {
            Author author = entityManager.find(Author.class, AUTHOR_ID, LockModeType.OPTIMISTIC);
            author.setFirstName(name);

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(timeToWaitInSeconds));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            entityManager.persist(author);
        });
    }

    private void execInTransaction(Runnable runnable) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                runnable.run();
            }
        });
    }
}
