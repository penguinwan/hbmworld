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

        def method1 = { varSession ->
            Stock stock = stockOf('susi')
            StockDailyRecord record1 = dailyRecordOf('01/01/2001')
            stock.stockDailyRecords.add(record1)
            StockDailyRecord record2 = dailyRecordOf('02/02/2001')
            stock.stockDailyRecords.add(record2)
            session.save(stock)
        }

        runInTransaction(method1, session)

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
