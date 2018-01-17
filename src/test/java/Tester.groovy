import com.penguinwan.hbmworld.Stock
import com.penguinwan.hbmworld.StockDailyRecord
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.cfg.Configuration
import org.junit.Test

class Tester {

    @Test
    void "sandbox"() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory()
        Session session = sessionFactory.openSession()
        Transaction transaction1 = session.beginTransaction()

        Stock stock1 = stockOf('1')
        StockDailyRecord record1 = dailyRecordOf('01/01/2000')
        record1.stock = stock1
        stock1.stockDailyRecords.add(record1)

        Stock stock2 = stockOf('2')
        StockDailyRecord record2 = dailyRecordOf('02/02/2000')
        record2.stock = stock2
        stock2.stockDailyRecords.add(record2)

        session.save(stock1)
        session.save(record1)

        session.save(stock2)
        session.save(record2)

        transaction1.commit()

        def method1 = { varSession ->
            StockDailyRecord record2Delta = varSession.get(StockDailyRecord.class, record2.recordId)
            println record2Delta.date.format('dd/MM/yyyy')
            record2Delta.stock = stock1
            varSession.update(record2Delta)
        }

        def method2 = { varSession ->
            Stock stock1Delta = varSession.get(Stock.class, stock1.stockId)
            stock1Delta.stockDailyRecords.add(record2)
            varSession.update(stock1Delta)
        }

        def method3 = { varSession ->
            Stock s1 = stockOf('susi')
            StockDailyRecord r1 = dailyRecordOf('03/03/2000')
            s1.stockDailyRecords.add(r1)
            r1.stock = s1
            varSession.save(s1)
        }

        runInTransaction(method3, session)

        session.close()
    }

    void runInTransaction(def method, def session) {
        Transaction transaction = session.beginTransaction()

        method(session)

        transaction.commit()
    }

    Stock stockOf(String name) {
        new Stock(
                stockCode: name,
                stockName: name
        )
    }

    StockDailyRecord dailyRecordOf(String date) {
        new StockDailyRecord(
                priceOpen: 1.99,
                priceClose: 2.99,
                date: new Date().parse('dd/MM/yyyy', date)
        )
    }
}
