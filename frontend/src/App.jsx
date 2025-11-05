import { useState, useEffect } from 'react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { Container, CssBaseline, Box } from '@mui/material';
import Header from './components/Header';
import WelcomePage from './components/WelcomePage';
import UploadForms from './components/UploadForms';
import ResultsSection from './components/ResultsSection';
import GeneralTaxChat from './components/GeneralTaxChat';
import IRSQuery from './components/IRSQuery';
import ProgressOverlay from './components/ProgressOverlay';
import ImageModal from './components/ImageModal';

const theme = createTheme({
  palette: {
    primary: {
      main: '#003d82',
      light: '#1565c0',
      dark: '#002952',
    },
    secondary: {
      main: '#00875a',
      light: '#00a86b',
      dark: '#006644',
    },
    background: {
      default: '#f5f7fa',
      paper: '#ffffff',
    },
    success: {
      main: '#00875a',
    },
    info: {
      main: '#003d82',
    },
  },
  typography: {
    fontFamily: '"Inter", "Segoe UI", "Roboto", sans-serif',
    h3: {
      fontWeight: 700,
      letterSpacing: '-0.02em',
    },
    h5: {
      fontWeight: 600,
    },
    h6: {
      fontWeight: 600,
    },
  },
  shape: {
    borderRadius: 16,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
          borderRadius: 12,
          padding: '10px 24px',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
        },
      },
    },
  },
});

function App() {
  const [view, setView] = useState('welcome'); // 'welcome', 'upload', 'chat', 'results', 'irs'
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [modalImage, setModalImage] = useState(null);

  useEffect(() => {
    const savedResults = sessionStorage.getItem('w2Results');
    if (savedResults) {
      setResults(JSON.parse(savedResults));
      setView('results');
      sessionStorage.removeItem('w2Results');
    }
  }, []);

  const handleBackToHome = () => {
    setView('welcome');
    setResults(null);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ 
        minHeight: '100vh',
        bgcolor: '#e8eef5',
        py: 6
      }}>
        <Container maxWidth="lg">
          <Box sx={{ 
            bgcolor: 'white', 
            borderRadius: 3, 
            p: { xs: 3, md: 5 },
            boxShadow: '0 2px 8px rgba(0,61,130,0.1)',
            border: '1px solid #d1dce6'
          }}>
            {view !== 'welcome' && <Header />}
            
            {view === 'welcome' && (
              <WelcomePage 
                onHasW2={() => setView('upload')} 
                onNoW2={() => setView('chat')}
                onQueryIRS={() => setView('irs')} 
              />
            )}
            
            {view === 'upload' && (
              <>
                <UploadForms setResults={setResults} setLoading={setLoading} />
                <Box sx={{ textAlign: 'center', mt: 3 }}>
                  <button onClick={handleBackToHome} style={{ 
                    background: 'none', 
                    border: 'none', 
                    color: '#1976d2', 
                    cursor: 'pointer',
                    textDecoration: 'underline'
                  }}>
                    ← Back to Home
                  </button>
                </Box>
              </>
            )}
            
            {view === 'chat' && (
              <GeneralTaxChat onBackToHome={handleBackToHome} />
            )}
            
            {view === 'irs' && (
              <IRSQuery onBack={handleBackToHome} />
            )}
            
            {view === 'results' && (
              <>
                <ResultsSection results={results} setModalImage={setModalImage} />
                <Box sx={{ textAlign: 'center', mt: 3 }}>
                  <button onClick={handleBackToHome} style={{ 
                    background: 'none', 
                    border: 'none', 
                    color: '#1976d2', 
                    cursor: 'pointer',
                    textDecoration: 'underline',
                    fontSize: '1rem'
                  }}>
                    ← Back to Home
                  </button>
                </Box>
              </>
            )}
          </Box>
        </Container>
      </Box>
      <ProgressOverlay open={loading} />
      <ImageModal image={modalImage} onClose={() => setModalImage(null)} />
    </ThemeProvider>
  );
}

export default App;
