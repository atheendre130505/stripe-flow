import { Routes, Route } from 'react-router-dom'
import { Layout } from '@/components/Layout'
import { Dashboard } from '@/pages/Dashboard'
import { Transactions } from '@/pages/Transactions'
import { Customers } from '@/pages/Customers'
import { Webhooks } from '@/pages/Webhooks'
import { ApiKeys } from '@/pages/ApiKeys'
import { Analytics } from '@/pages/Analytics'

function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/transactions" element={<Transactions />} />
        <Route path="/customers" element={<Customers />} />
        <Route path="/webhooks" element={<Webhooks />} />
        <Route path="/api-keys" element={<ApiKeys />} />
        <Route path="/analytics" element={<Analytics />} />
      </Routes>
    </Layout>
  )
}

export default App

