import { Box, Typography, Button, Paper } from '@mui/material';
import { Description, Chat, Security, Speed, Psychology, Search } from '@mui/icons-material';

export default function WelcomePage({ onHasW2, onNoW2, onQueryIRS }) {
  return (
    <Box sx={{ textAlign: 'center', py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Box sx={{ 
          display: 'inline-flex',
          bgcolor: '#003d82',
          color: 'white',
          p: 2,
          borderRadius: 2,
          mb: 2,
          boxShadow: '0 4px 12px rgba(0,61,130,0.25)'
        }}>
          <Description sx={{ fontSize: 48 }} />
        </Box>
        <Typography variant="h3" sx={{ 
          mb: 1, 
          fontWeight: 800,
          color: '#003d82',
          fontSize: { xs: '1.75rem', md: '2.25rem' }
        }}>
          Tax Advisor Pro
        </Typography>
        <Typography variant="body1" sx={{ mb: 0.5, color: '#5f6368', fontWeight: 500, maxWidth: 650, mx: 'auto' }}>
          AI-Powered Tax Document Analysis & Advisory
        </Typography>
        <Typography variant="body2" sx={{ color: '#9e9e9e', maxWidth: 600, mx: 'auto' }}>
          Supports W-2, 1099-NEC, and more tax forms
        </Typography>
      </Box>

      <Box sx={{ maxWidth: 950, mx: 'auto', mb: 6, display: 'flex', gap: 3, justifyContent: 'center', flexWrap: 'wrap' }}>
        <Paper elevation={0} sx={{ 
          p: 4, 
          flex: '1 1 260px', 
          maxWidth: 290, 
          bgcolor: 'white', 
          border: '2px solid #e3f2fd',
          textAlign: 'center', 
          borderRadius: 3,
          transition: 'all 0.3s ease',
          '&:hover': {
            transform: 'translateY(-8px)',
            boxShadow: '0 12px 24px rgba(0,61,130,0.15)',
            borderColor: '#003d82'
          }
        }}>
          <Box sx={{ bgcolor: '#e3f2fd', borderRadius: 2, p: 2, display: 'inline-flex', mb: 2 }}>
            <Speed sx={{ fontSize: 48, color: '#003d82' }} />
          </Box>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 1.5, color: '#003d82', fontSize: '1.25rem' }}>Fast Processing</Typography>
          <Typography variant="body2" sx={{ color: '#5f6368', lineHeight: 1.6 }}>Extract W-2, 1099-NEC data in seconds with AI</Typography>
        </Paper>
        <Paper elevation={0} sx={{ 
          p: 4, 
          flex: '1 1 260px', 
          maxWidth: 290, 
          bgcolor: 'white', 
          border: '2px solid #e8f5e9',
          textAlign: 'center', 
          borderRadius: 3,
          transition: 'all 0.3s ease',
          '&:hover': {
            transform: 'translateY(-8px)',
            boxShadow: '0 12px 24px rgba(0,135,90,0.15)',
            borderColor: '#00875a'
          }
        }}>
          <Box sx={{ bgcolor: '#e8f5e9', borderRadius: 2, p: 2, display: 'inline-flex', mb: 2 }}>
            <Security sx={{ fontSize: 48, color: '#00875a' }} />
          </Box>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 1.5, color: '#00875a', fontSize: '1.25rem' }}>100% Secure</Typography>
          <Typography variant="body2" sx={{ color: '#5f6368', lineHeight: 1.6 }}>Bank-level encryption keeps your data safe and confidential</Typography>
        </Paper>
        <Paper elevation={0} sx={{ 
          p: 4, 
          flex: '1 1 260px', 
          maxWidth: 290, 
          bgcolor: 'white', 
          border: '2px solid #f5f5f5',
          textAlign: 'center', 
          borderRadius: 3,
          transition: 'all 0.3s ease',
          '&:hover': {
            transform: 'translateY(-8px)',
            boxShadow: '0 12px 24px rgba(66,66,66,0.15)',
            borderColor: '#757575'
          }
        }}>
          <Box sx={{ bgcolor: '#f5f5f5', borderRadius: 2, p: 2, display: 'inline-flex', mb: 2 }}>
            <Psychology sx={{ fontSize: 48, color: '#424242' }} />
          </Box>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 1.5, color: '#424242', fontSize: '1.25rem' }}>Expert Advice</Typography>
          <Typography variant="body2" sx={{ color: '#5f6368', lineHeight: 1.6 }}>Get personalized tax recommendations from AI advisor</Typography>
        </Paper>
      </Box>

      <Paper 
        elevation={0}
        sx={{ 
          maxWidth: 900, 
          mx: 'auto', 
          p: 6,
          bgcolor: 'white',
          border: '3px solid #003d82',
          borderRadius: 4,
          boxShadow: '0 8px 24px rgba(0,61,130,0.12)'
        }}
      >
        <Typography variant="h4" sx={{ mb: 2, fontWeight: 700, color: '#003d82' }}>
          How can I assist you today?
        </Typography>
        <Typography variant="body1" sx={{ mb: 5, color: '#5f6368' }}>
          Choose an option below to get started
        </Typography>

        <Box sx={{ display: 'flex', gap: 3, justifyContent: 'center', flexWrap: 'wrap', maxWidth: 850, mx: 'auto' }}>
          <Button
            variant="contained"
            size="large"
            startIcon={<Description sx={{ fontSize: 28 }} />}
            onClick={onHasW2}
            sx={{
              py: 3,
              px: 6,
              fontSize: '1.15rem',
              fontWeight: 700,
              minWidth: 260,
              bgcolor: '#003d82',
              borderRadius: 3,
              boxShadow: '0 4px 12px rgba(0,61,130,0.3)',
              '&:hover': {
                bgcolor: '#002952',
                boxShadow: '0 8px 20px rgba(0,61,130,0.4)',
                transform: 'translateY(-4px)',
              },
              transition: 'all 0.3s ease'
            }}
          >
            Upload Tax Documents
          </Button>

          <Button
            variant="outlined"
            size="large"
            startIcon={<Chat sx={{ fontSize: 28 }} />}
            onClick={onNoW2}
            sx={{
              py: 3,
              px: 6,
              fontSize: '1.15rem',
              fontWeight: 700,
              minWidth: 260,
              borderWidth: 3,
              borderRadius: 3,
              borderColor: '#00875a',
              color: '#00875a',
              '&:hover': {
                borderWidth: 3,
                bgcolor: '#e8f5e9',
                borderColor: '#006644',
                transform: 'translateY(-4px)',
                boxShadow: '0 8px 20px rgba(0,135,90,0.2)',
              },
              transition: 'all 0.3s ease'
            }}
          >
            Ask Tax Questions
          </Button>

          <Button
            variant="outlined"
            size="large"
            startIcon={<Search sx={{ fontSize: 28 }} />}
            onClick={onQueryIRS}
            sx={{
              py: 3,
              px: 6,
              fontSize: '1.15rem',
              fontWeight: 700,
              minWidth: 260,
              borderWidth: 3,
              borderRadius: 3,
              borderColor: '#ff9800',
              color: '#ff9800',
              '&:hover': {
                borderWidth: 3,
                bgcolor: '#fff3e0',
                borderColor: '#f57c00',
                transform: 'translateY(-4px)',
                boxShadow: '0 8px 20px rgba(255,152,0,0.2)',
              },
              transition: 'all 0.3s ease'
            }}
          >
            Query IRS 2025
          </Button>
        </Box>
      </Paper>

      <Box sx={{ mt: 6, textAlign: 'center' }}>
        <Box sx={{ display: 'inline-flex', alignItems: 'center', gap: 1, bgcolor: '#e8f5e9', px: 3, py: 1.5, borderRadius: 3, border: '1px solid #00875a' }}>
          <Security sx={{ fontSize: 20, color: '#00875a' }} />
          <Typography variant="body2" sx={{ color: '#00875a', fontWeight: 600 }}>
            Your information is encrypted and remains completely confidential
          </Typography>
        </Box>
      </Box>
    </Box>
  );
}
