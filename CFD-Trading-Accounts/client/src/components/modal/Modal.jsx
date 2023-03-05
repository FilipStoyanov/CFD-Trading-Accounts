import * as React from "react";
import Box from "@mui/material/Box";
import { Modal, Button, Typography } from "@mui/material/";

const T212Modal = () => {
  const [open, setOpen] = React.useState(false);
  const handleClose = () => {
    setOpen(!open);
    setTimeout(() => {
      window.location.reload();
    }, 2000)
  };

  return (
    <Modal
      open={!open}
      onClose={handleClose}
      aria-labelledby="parent-modal-title"
      aria-describedby="parent-modal-description"
    >
      <Box sx={[styles.content, { width: 300 }]}>
        <Typography textAlign="center" variant="h5" fontWeight="bold" mb={2}>
          Error
        </Typography>
        <Typography textAlign="center" fontSize={16} mb={2}>
          Oops, something went wrong. Please try again
        </Typography>
        <Button sx = {{backgroundColor: "#ffffff", width: "300px", '&:hover': {backgroundColor: '#ffffff'}}} variant="outlined" onClick={handleClose}>OK</Button>
      </Box>
    </Modal>
  );
};

export default T212Modal;

const styles = {
  content: {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: 400,
    bgcolor: "background.paper",
    border: "2px solid #00a7e1",
    backgroundColor: "#00a7e1",
    boxShadow: 24,
    padding: "50px",
    color: "#ffffff",
  },
};
