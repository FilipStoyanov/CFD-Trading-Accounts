import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { alpha } from "@mui/material/styles";
import Box from "@mui/material/Box";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import TableSortLabel from "@mui/material/TableSortLabel";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Paper from "@mui/material/Paper";
import CancelIcon from "@mui/icons-material/Cancel";
import { visuallyHidden } from "@mui/utils";
import { useSelector } from "react-redux";
import { IconButton, Button, Modal, Grid } from "@mui/material";
import { closeMarketPosition } from "../../requests";
import StraightIcon from "@mui/icons-material/Straight";
import SouthIcon from "@mui/icons-material/South";

function createData(
  ticker,
  quantity,
  type,
  price,
  currentPrice,
  margin,
  result
) {
  return {
    ticker,
    quantity,
    type,
    price,
    currentPrice,
    margin,
    result,
  };
}

function descendingComparator(a, b, orderBy) {
  if (b[orderBy] < a[orderBy]) {
    return -1;
  }
  if (b[orderBy] > a[orderBy]) {
    return 1;
  }
  return 0;
}

function getComparator(order, orderBy) {
  return order === "desc"
    ? (a, b) => descendingComparator(a, b, orderBy)
    : (a, b) => -descendingComparator(a, b, orderBy);
}

function stableSort(array, comparator) {
  const stabilizedThis = array.map((el, index) => [el, index]);
  stabilizedThis.sort((a, b) => {
    const order = comparator(a[0], b[0]);
    if (order !== 0) {
      return order;
    }
    return a[1] - b[1];
  });
  return stabilizedThis.map((el) => el[0]);
}

const headCells = [
  {
    id: "instrument",
    numeric: false,
    disablePadding: true,
    label: "INSTRUMENT",
  },
  {
    id: "quantity",
    numeric: true,
    disablePadding: false,
    label: "QUANTITY",
  },
  {
    id: "direction",
    numeric: false,
    disablePadding: false,
    label: "DIRECTION",
  },
  {
    id: "price",
    numeric: true,
    disablePadding: false,
    label: "PRICE",
  },
  {
    id: "currentPrice",
    numeric: true,
    disablePadding: false,
    label: "CURRENT PRICE",
  },
  {
    id: "margin",
    numeric: true,
    disablePadding: false,
    label: "MARGIN",
  },
  {
    id: "result",
    numeric: true,
    disablePadding: false,
    label: "RESULT",
  },
  {
    id: "close",
    numeric: false,
    disablePadding: false,
    label: "",
  },
];

function EnhancedTableHead(props) {
  const {
    onSelectAllClick,
    order,
    orderBy,
    numSelected,
    rowCount,
    onRequestSort,
  } = props;
  const createSortHandler = (property) => (event) => {
    onRequestSort(event, property);
  };

  return (
    <TableHead>
      <TableRow>
        {headCells.map((headCell, index) => (
          <TableCell
            key={headCell.id}
            align={index === 0 ? "left" : "right"}
            padding={headCell.disablePadding ? "none" : "normal"}
            sortDirection={orderBy === headCell.id ? order : false}
          >
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : "asc"}
              onClick={createSortHandler(headCell.id)}
              sx={styles.cell}
            >
              {headCell.label}
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === "desc" ? "sorted descending" : "sorted ascending"}
                </Box>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
      </TableRow>
    </TableHead>
  );
}

EnhancedTableHead.propTypes = {
  numSelected: PropTypes.number.isRequired,
  onRequestSort: PropTypes.func.isRequired,
  onSelectAllClick: PropTypes.func.isRequired,
  order: PropTypes.oneOf(["asc", "desc"]).isRequired,
  orderBy: PropTypes.string.isRequired,
  rowCount: PropTypes.number.isRequired,
};

function EnhancedTableToolbar(props) {
  const { numSelected } = props;

  return (
    <Toolbar
      sx={{
        pl: { sm: 2 },
        pr: { xs: 1, sm: 1 },
        ...(numSelected > 0 && {
          bgcolor: (theme) =>
            alpha(
              theme.palette.primary.main,
              theme.palette.action.activatedOpacity
            ),
        }),
      }}
    >
      {numSelected > 0 ? (
        <Typography
          sx={{ flex: "1 1 100%" }}
          color="inherit"
          variant="subtitle1"
          component="div"
        >
          {numSelected} selected
        </Typography>
      ) : (
        <Typography
          sx={{ flex: "1 1 100%" }}
          variant="h6"
          id="tableTitle"
          component="div"
          fontSize={22}
        >
          Positions
        </Typography>
      )}
    </Toolbar>
  );
}

EnhancedTableToolbar.propTypes = {
  numSelected: PropTypes.number.isRequired,
};

export default function PositionsTable({
  rows,
  setRow,
  setData,
  handleOnRemove,
}) {
  const [order, setOrder] = React.useState("asc");
  const [orderBy, setOrderBy] = React.useState("quantity");
  const [selected, setSelected] = React.useState([]);
  const [page, setPage] = React.useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(5);
  const [chosenRow, setChosenRow] = useState();
  const user = useSelector((state) => state.user.user);
  const [open, setOpen] = React.useState(false);
  const [positionForClosing, setPositionForClosing] = useState();
  const closeConfirm = () => {
    setOpen(!open);
  };

  const confirm = () => {
    handleOnRemove(positionForClosing);
    closePosition(positionForClosing);
    setPositionForClosing(null);
    setOpen(false);
  };

  const handleRequestSort = (event, property) => {
    const isAsc = orderBy === property && order === "asc";
    setOrder(isAsc ? "desc" : "asc");
    setOrderBy(property);
  };

  const closePosition = async (position) => {
    const res = await closeMarketPosition(position, user.id);
    if (res.status === 200) {
      const filteredData = [
        ...rows.filter(
          (item) => item.ticker != position.ticker || item.type != position.type
        ),
      ];
      setData(filteredData);
    }
  };

  const handleSelectAllClick = (event) => {
    if (event.target.checked) {
      const newSelected = rows.map((n) => n.ticker);
      setSelected(newSelected);
      return;
    }
    setSelected([]);
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const isSelected = (ticker) => selected.indexOf(ticker) !== -1;

  const emptyRows =
    page > 0 ? Math.max(0, (1 + page) * rowsPerPage - rows.length) : 0;

  return (
    <Box sx={{ width: "100%" }}>
      <Paper sx={{ width: "100%", mb: 2, borderTop: "7px solid #d3d4d9" }}>
        <EnhancedTableToolbar numSelected={selected.length} />
        <TableContainer sx={{ maxHeight: "100px", overflowY: "auto" }}>
          <Table
            sx={[
              styles.table,
              { minWidth: 750, padding: "0px 18px", overflow: "auto" },
            ]}
            aria-labelledby="tableTitle"
            size={"small"}
          >
            <EnhancedTableHead
              numSelected={selected.length}
              order={order}
              orderBy={orderBy}
              onSelectAllClick={handleSelectAllClick}
              onRequestSort={handleRequestSort}
              rowCount={rows.length}
            />
            <TableBody>
              {stableSort(rows, getComparator(order, orderBy))
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((row, index) => {
                  return (
                    <TableRow
                      hover
                      onClick={() => {
                        setChosenRow(row.ticker);
                        setRow(row);
                      }}
                      role="checkbox"
                      tabIndex={-1}
                      key={row.ticker + "_" + row.type}
                      sx={styles.row}
                    >
                      <TableCell component="th" scope="row" padding="none">
                        {row.ticker}
                      </TableCell>
                      <TableCell align="right">{row.quantity}</TableCell>
                      <TableCell align="right">
                        {row.type === "LONG" ? "Buy" : "Sell"}
                      </TableCell>
                      <TableCell align="right">
                        {row.price.toFixed(5)}
                      </TableCell>
                      <TableCell align="right">
                        {row.currentPrice.toFixed(4)}
                      </TableCell>
                      <TableCell align="right">
                        {row.margin ? row.margin.toFixed(5) : ""}
                      </TableCell>
                      <TableCell
                        align="right"
                        sx={
                          row.result < 0
                            ? { color: "#fa6464" }
                            : { color: "#3f3f3f" }
                        }
                      >
                        {row.result ? row.result.toFixed(2) : ""}
                      </TableCell>
                      <TableCell>
                        <IconButton
                          sx={styles.close}
                          onClick={(e) => {
                            e.stopPropagation();
                            setPositionForClosing(row);
                            setOpen(!open);
                          }}
                        >
                          <CancelIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  );
                })}
              {emptyRows > 0 && (
                <TableRow
                  style={{
                    height: 33 * emptyRows,
                  }}
                >
                  <TableCell colSpan={6} />
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          rowsPerPageOptions={[5]}
          component="div"
          count={rows.length}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={handleChangePage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
      </Paper>
        <Modal
          open={open}
          onClose={closeConfirm}
          aria-labelledby="parent-modal-title"
          aria-describedby="parent-modal-description"
        >
          <Box sx={[styles.content, { width: 300 }]}>
            <Typography
              textAlign="center"
              variant="h5"
              fontWeight="bold"
              mb={2}
            >
              Please Confirm
            </Typography>
            <Typography textAlign="center" fontSize={16} mb={2}>
              Are you sure you want to close this position with {positionForClosing ? positionForClosing.ticker : ""}
            </Typography>
            <Grid container justifyContent={"space-between"}>
              <Grid item>
                <Button
                  sx={{
                    backgroundColor: "#ffffff",
                    width: "140px",
                    color: "#00a7e1",
                    "&:hover": { backgroundColor: "#14c4ff", color: "#ffffff" },
                  }}
                  variant="outlined"
                  onClick={confirm}
                >
                  Confirm
                </Button>
              </Grid>
              <Grid item>
                <Button
                  sx={{
                    backgroundColor: "#ffffff",
                    width: "140px",
                    color: "#00a7e1",
                    "&:hover": { backgroundColor: "#14c4ff", color: "#ffffff" },
                  }}
                  variant="outlined"
                  onClick={closeConfirm}
                >
                  Cancel
                </Button>
              </Grid>
            </Grid>
          </Box>
        </Modal>
    </Box>
  );
}

const styles = {
  invisible: {
    visibility: "hidden",
  },
  cell: {
    color: "#747980",
  },
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
  row: {
    paddingLeft: 10,
    paddingRight: 10,
  },
  table: {
    "&.MuiTable-root": {
      borderCollapse: "unset",
    },
  },
  row: {
    position: "relative",
  },
  close: {
    position: "absolute",
    top: -4,
    right: 0,
  },
};
